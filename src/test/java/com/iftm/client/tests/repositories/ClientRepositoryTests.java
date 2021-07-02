package com.iftm.client.tests.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.tests.factory.ClientFactory;

@DataJpaTest
public class ClientRepositoryTests {
	
	@Autowired
	private ClientRepository repository;
	
	private long existingId;
	private long nonexistingId;
	private long countTotalClients;
	private long countClientByIncome;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonexistingId = Long.MAX_VALUE;
		countTotalClients = 12L;
		countClientByIncome = 5L;
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
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		Client client = ClientFactory.createClient();
		client.setId(null);
		
		client = repository.save(client);
		Optional<Client> result = repository.findById(client.getId());
		
		
		Assertions.assertNotNull(client.getId());
		Assertions.assertEquals(countTotalClients + 1, client.getId());
		Assertions.assertTrue(result.isPresent());
		Assertions.assertSame(result.get(), client);
	}
	
	@Test
	public void findByIncomeShouldReturnClientsWhenClientIncomeIsGreaterThanOrEqualsToValue() {
		Double income = 4000.0;
		PageRequest pageRequest = PageRequest.of(0, 10);
		
		Page<Client> result = repository.findByIncome(income, pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientByIncome, result.getTotalElements());
	}
	
	// Exercicios
	@Test
	public void findByNameContainingIgnoreCaseShouldReturnClientWhenClientIsExistent() {
		String name = "Chimamanda Adichie";
		List<Client> result = repository.findByNameContainingIgnoreCase(name);
		Assertions.assertEquals(result.get(0).getName(), name);
	}
	
	@Test
	public void findByNameContainingIgnoreCaseShouldReturnClientWhenClientIsExistentIgnoringCase() {
		String name = "Chimamanda Adichie";
		String nameCase = "ChImAmAnDa AdIcHiE";
		List<Client> result = repository.findByNameContainingIgnoreCase(nameCase);
		Assertions.assertEquals(name, result.get(0).getName());
	}
	
	@Test
	public void findByNameContainingIgnoreCaseShouldReturnAllClientsWhenEmpty() {
		String name = "";
		List<Client> result = repository.findByNameContainingIgnoreCase(name);
		List<Client> all = repository.findAll();
		Assertions.assertEquals(all.size(), result.size());
	}
	
	@Test
	public void findByBirthDateShouldNotReturnWhenWrongBirthDate() {
		Date date = new Date();
		List<Client> result = repository.findByBirthDate(date.toInstant());
		Assertions.assertEquals(0, result.size());
	}
	
	@Test
	public void saveShouldChangeName() {
		List<Client> clients = repository.findAll();
		Client client = clients.get(0);
		String name = "Elian";
		client.setName(name);
		repository.save(client);
		client = repository.findById(client.getId()).get();
		Assertions.assertEquals(name, client.getName());
	}
	
	@Test
	public void saveShouldChangeIncome() {
		List<Client> clients = repository.findAll();
		Client client = clients.get(0);
		Double income = 5000.0;
		client.setIncome(income);
		repository.save(client);
		client = repository.findById(client.getId()).get();
		Assertions.assertEquals(income, client.getIncome());
	}
}
