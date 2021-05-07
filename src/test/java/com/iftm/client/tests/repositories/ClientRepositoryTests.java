package com.iftm.client.tests.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;

@DataJpaTest
public class ClientRepositoryTests {
	
	@Autowired
	private ClientRepository repository;
	
	private long existingId;
	private long nonexistingId;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonexistingId = Long.MAX_VALUE;
	}
	
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existingId);
		
		Optional<Client> result = repository.findById(existingId);
		
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonexistingId);
		});
	}
	
}
