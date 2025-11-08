package com.example.beneficio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pageable {

	private int page;
	private int size;

	public static Pageable of(int page, int size) {
		return new Pageable(page, size);
	}

	public int getOffset() {
		return page * size;
	}
}
