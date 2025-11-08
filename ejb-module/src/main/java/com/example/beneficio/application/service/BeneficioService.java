package com.example.beneficio.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.example.beneficio.domain.exception.BeneficioException;
import com.example.beneficio.domain.model.Beneficio;
import com.example.beneficio.domain.model.Page;
import com.example.beneficio.domain.model.Pageable;
import com.example.beneficio.domain.port.input.BeneficioUseCase;
import com.example.beneficio.domain.port.output.BeneficioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BeneficioService implements BeneficioUseCase {

	private final BeneficioRepository repository;

	private static final ConcurrentHashMap<Long, ReentrantLock> locksCache = new ConcurrentHashMap<>();
	private static final long LOCK_TIMEOUT_SECONDS = 5;

	@Override
	public Page<Beneficio> listarTodos(Pageable pageable) {
		return repository.listarTodos(pageable);
	}

	@Override
	public List<Beneficio> buscarAtivosPorNome(String nome) {
		return repository.buscarAtivosPorNome(nome);
	}

	@Override
	public Optional<Beneficio> buscarPorId(Long id) {
		return repository.buscarPorId(id);
	}

	@Override
	public Beneficio criar(Beneficio beneficio) {
		beneficio.setId(null);
		return repository.salvar(beneficio);
	}

	@Override
	public Beneficio atualizar(Long id, Beneficio beneficio) {
		Beneficio existente = repository.buscarPorId(id)
				.orElseThrow(() -> new BeneficioException("Benefício não encontrado: " + id));

		existente.setNome(beneficio.getNome());
		existente.setDescricao(beneficio.getDescricao());
		existente.setValor(beneficio.getValor());
		existente.setAtivo(beneficio.getAtivo());

		return repository.salvar(existente);
	}

	@Override
	public void remover(Long id) {
		repository.buscarPorId(id).orElseThrow(() -> new BeneficioException("Benefício não encontrado: " + id));
		repository.remover(id);
	}

	@Override
	public void transferir(Long fromId, Long toId, BigDecimal amount) {
		validarParametros(fromId, toId, amount);

		Long firstLockId = fromId < toId ? fromId : toId;
		Long secondLockId = fromId < toId ? toId : fromId;

		ReentrantLock firstLock = getLock(firstLockId);
		ReentrantLock secondLock = getLock(secondLockId);

		boolean firstLockAcquired = false;
		boolean secondLockAcquired = false;

		try {
			if (!firstLock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
				throw new BeneficioException(
						"Timeout ao adquirir lock para transferência. Tente novamente em alguns instantes.");
			}
			firstLockAcquired = true;

			if (!secondLock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
				throw new BeneficioException(
						"Timeout ao adquirir lock para transferência. Tente novamente em alguns instantes.");
			}
			secondLockAcquired = true;

			executarTransferencia(fromId, toId, amount);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new BeneficioException("Transferência interrompida");
		} finally {
			if (secondLockAcquired) {
				secondLock.unlock();
			}
			if (firstLockAcquired) {
				firstLock.unlock();
			}
		}
	}

	private ReentrantLock getLock(Long id) {
		return locksCache.computeIfAbsent(id, k -> new ReentrantLock());
	}

	private void executarTransferencia(Long fromId, Long toId, BigDecimal amount) {
		Beneficio origem = repository.buscarPorIdComLock(fromId)
				.orElseThrow(() -> new BeneficioException("Benefício não encontrado: " + fromId));

		Beneficio destino = repository.buscarPorIdComLock(toId)
				.orElseThrow(() -> new BeneficioException("Benefício não encontrado: " + toId));

		validarBeneficioAtivo(origem, fromId);
		validarBeneficioAtivo(destino, toId);
		validarSaldoSuficiente(origem, amount);

		origem.decrementar(amount);
		destino.incrementar(amount);

		repository.salvar(origem);
		repository.salvar(destino);
	}

	private void validarParametros(Long fromId, Long toId, BigDecimal amount) {
		if (fromId == null || toId == null) {
			throw new BeneficioException("IDs de origem e destino não podem ser nulos");
		}

		if (fromId.equals(toId)) {
			throw new BeneficioException("Não é possível transferir para o mesmo benefício");
		}

		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new BeneficioException("Valor da transferência deve ser maior que zero");
		}
	}

	private void validarBeneficioAtivo(Beneficio beneficio, Long id) {
		if (!beneficio.isAtivo()) {
			throw new BeneficioException("Benefício não está ativo: " + id);
		}
	}

	private void validarSaldoSuficiente(Beneficio origem, BigDecimal amount) {
		if (!origem.temSaldoSuficiente(amount)) {
			throw new BeneficioException(
					String.format("Saldo insuficiente no benefício %d. Saldo atual: %s, Valor solicitado: %s",
							origem.getId(), origem.getValor(), amount));
		}
	}
}
