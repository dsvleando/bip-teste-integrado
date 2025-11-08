package com.example.beneficio.infrastructure.adapter.output;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.beneficio.domain.model.Beneficio;
import com.example.beneficio.domain.model.Page;
import com.example.beneficio.domain.model.Pageable;
import com.example.beneficio.domain.port.output.BeneficioRepository;
import com.example.beneficio.infrastructure.mapper.BeneficioMapper;
import com.example.beneficio.infrastructure.persistence.entity.BeneficioEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaBeneficioRepository implements BeneficioRepository {

	@PersistenceContext
	private final EntityManager entityManager;

	private final BeneficioMapper mapper;

	@Override
	public Optional<Beneficio> buscarPorId(Long id) {
		BeneficioEntity entity = entityManager.find(BeneficioEntity.class, id);
		return Optional.ofNullable(entity).map(mapper::toDomain);
	}

	@Override
	public Optional<Beneficio> buscarPorIdComLock(Long id) {
		BeneficioEntity entity = entityManager.find(BeneficioEntity.class, id, LockModeType.OPTIMISTIC);
		return Optional.ofNullable(entity).map(mapper::toDomain);
	}

	@Override
	public Page<Beneficio> listarTodos(Pageable pageable) {
		TypedQuery<BeneficioEntity> query = entityManager.createQuery("SELECT b FROM BeneficioEntity b",
				BeneficioEntity.class);

		long totalElements = entityManager.createQuery("SELECT COUNT(b) FROM BeneficioEntity b", Long.class)
				.getSingleResult();

		List<Beneficio> content = query.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getSize())
				.getResultList().stream().map(mapper::toDomain).collect(Collectors.toList());

		int totalPages = (int) Math.ceil((double) totalElements / pageable.getSize());
		boolean first = pageable.getPage() == 0;
		boolean last = pageable.getPage() >= totalPages - 1;

		return Page.<Beneficio>builder().content(content).page(pageable.getPage()).size(pageable.getSize())
				.totalElements(totalElements).totalPages(totalPages).first(first).last(last).build();
	}

	@Override
	public List<Beneficio> buscarAtivosPorNome(String nome) {
		String jpql = "SELECT b FROM BeneficioEntity b WHERE b.ativo = true AND UPPER(b.nome) LIKE UPPER(:nome) ORDER BY b.nome";
		TypedQuery<BeneficioEntity> query = entityManager.createQuery(jpql, BeneficioEntity.class);
		query.setParameter("nome", "%" + nome + "%");
		query.setMaxResults(20);
		return query.getResultList().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Beneficio salvar(Beneficio beneficio) {
		BeneficioEntity entity = mapper.toEntity(beneficio);
		BeneficioEntity saved = entityManager.merge(entity);
		return mapper.toDomain(saved);
	}

	@Override
	public void remover(Long id) {
		BeneficioEntity entity = entityManager.find(BeneficioEntity.class, id);
		if (entity != null) {
			entityManager.remove(entity);
		}
	}
}
