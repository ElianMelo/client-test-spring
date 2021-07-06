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
import com.iftm.client.tests.factory.ClientFactory;

@SpringBootTest
@Transactional
public class ClienteServiceIT {
	
	@Autowired
	private ClientService service;
	
	private long existingId;
	private long specificId;
	private long nonExistingId;
	private PageRequest pageRequest;
	private long countClientByIncome;
	private long countTotalClient;
	private String specificName;
	private String specificCpf;
	private ClientDTO clientDTO;

	@BeforeEach
	void setUp() throws Exception {
		clientDTO = ClientFactory.createClientDTO();
		existingId = 1L;
		specificId = 11L;
		specificName = "Silvio Almeida";
		specificCpf = "10164334861";
		nonExistingId = 1000L;
		countClientByIncome = 5L;
		countTotalClient = 12L;
		pageRequest = PageRequest.of(0, 6);
	}
	
	/*
	 * Implementar um teste que ao receber um id existente deve excluir o cliente com o
	código do id e verificar se realmente decrementou o número de clientes incluídos na
	base de dados.
	 * */
	@Test
	public void deleteShouldDecreaseDatabaseValue() {
		int endValue;
		service.delete(existingId);
		List<ClientDTO> clients = service.findAll();
		endValue = clients.size();
		Assertions.assertTrue(endValue == countTotalClient - 1);
	}
	
	/*
	 * Implementar um teste que deverá testar o findById. Para o teste, você terá que
	entrar com o código de um cliente existente e verificar se o nome e CPF do cliente
	são correspondentes.
	 * */
	@Test
	public void findByIdShouldReturnSpecificValue() {
		ClientDTO client = service.findById(specificId);
		Assertions.assertEquals(specificName, client.getName());
		Assertions.assertEquals(specificCpf, client.getCpf());
	}
	/*
	 * Implementar um teste que deverá testar o insert. Para o teste, você deverá criar um
	novo cliente usando o padrão fábrica e inserir o cliente. Em seguida você deverá
	verificar o findAll e verificar se o número de clientes será incrementado na base de
	dados.
	 * */
	@Test
	public void insertShouldIncreaseDatabaseValue() {
		int endValue;
		List<ClientDTO> clients = service.findAll();
		service.insert(clientDTO);
		clients = service.findAll();
		endValue = clients.size();
		Assertions.assertTrue(endValue == countTotalClient + 1);
	}
	
	/*
	 *Implementar um teste que deverá testar o update. Para o teste, você deverá
	atualizar os dados de um cliente existente e em seguida você deverá verificar se os
	dados do cliente foram atualizados. Lembrando que o service update retorna os
	dados do cliente através do ClientDTO. 
	 * */
	@Test
	public void updateShouldChangeValue() {
		ClientDTO client = service.findById(existingId);
		ClientDTO clientChanged = service.update(existingId, clientDTO);

		Assertions.assertEquals(client.getId(), clientChanged.getId());
		/* Poder ocasionar em erro para alterações de alguns atributos */
		Assertions.assertNotEquals(client.getName(), clientChanged.getName());
		Assertions.assertNotEquals(client.getBirthDate(), clientChanged.getBirthDate());
		Assertions.assertFalse(client.getCpf().equals(clientChanged.getCpf()));
		Assertions.assertFalse(client.getChildren() == clientChanged.getChildren());
		Assertions.assertFalse(client.getIncome() == clientChanged.getIncome());
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
