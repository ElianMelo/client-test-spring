package com.iftm.client.tests.integration;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ClienteServiceIT {
	
	@Autowired
	private ClientService service;
	
	private long existingId;
	private long nonExistingId;
	private PageRequest pageRequest;
	private long countClientByIncome;
	private long countTotalClient;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countClientByIncome = 5L;
		countTotalClient = 12L;
		pageRequest = PageRequest.of(0, 6);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
	
	@Test
	public void findByIncomeShouldReturnClientsWhenClientIncomeIsGreaterThanOrEqualsToValue() {
		Double income = 4000.0;
		
		Page<ClientDTO> result = service.findByIncome(income, pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientByIncome, result.getTotalElements());
	}
	
	@Test
	public void findAllShouldReturnAllClients() {
		List<ClientDTO> result = service.findAll();
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countTotalClient, result.size());
	}
}
