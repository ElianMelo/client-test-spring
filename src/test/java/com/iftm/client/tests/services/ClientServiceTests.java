package com.iftm.client.tests.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.DatabaseException;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import com.iftm.client.tests.factory.ClientFactory;;

@ExtendWith(SpringExtension.class)
public class ClientServiceTests {
	@InjectMocks
	private ClientService service;
	
	@Mock
	private ClientRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private Client client;
	private ClientDTO clientDTO;
	private PageRequest pageRequest;
	private List<Client> fakeList;
	private Page<Client> pageMock;
	private Double income;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 5L;
		
		client = ClientFactory.createClient();
		clientDTO = ClientFactory.createClientDTO();
		
		pageRequest = PageRequest.of(0, 12, Direction.valueOf("ASC"), "name");
		fakeList = new ArrayList<>();
		fakeList.add(client);
		pageMock = new PageImpl<Client>(fakeList);
		income = 1500.0;
		
		// Configurando comportamento para o meu mock
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(client));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		// Atividade: testes de service com Mockito
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingId);
		
		Mockito.when(repository.findAll(pageRequest)).thenReturn(pageMock);
		Mockito.when(repository.findByIncome(ArgumentMatchers.anyDouble(), ArgumentMatchers.any()))
			.thenReturn(pageMock);
		Mockito.when(repository.save(client)).thenReturn(client);
		Mockito.when(repository.getOne(existingId)).thenReturn(client);
		
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).getOne(nonExistingId);
		
		PageRequest pageRequestTTT = PageRequest.of(0, 10);
		
		Page<Client> result = repository.findAll(pageRequestTTT);
		
		Mockito.doReturn(result).when(repository).findAll(pageRequestTTT);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExistis() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdHasDependencyIntegrity() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void findAllPagedShouldReturnPageAndCallFindAll() {
		Assertions.assertNotNull(service.findAllPaged(pageRequest));
		
		Mockito.verify(repository, Mockito.times(1)).findAll(pageRequest);	
	}
	
	@Test
	public void findByIncomeShouldReturnPageAndCallFindByIncome() {
		Assertions.assertNotNull(service.findByIncome(income, pageRequest));
		
		Mockito.verify(repository, Mockito.times(1)).findByIncome(income, pageRequest);	
	}
	
	@Test
	public void findByIdShouldReturnClientDTOWhenIdExists() {
		ClientDTO client = service.findById(existingId);
		Assertions.assertTrue(client instanceof ClientDTO);
		
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
	}
	
	@Test
	public void updateShouldReturnClientDTOWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.update(existingId, clientDTO);
		});
		
		Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
		Mockito.verify(repository, Mockito.times(1)).save(client);
	}
	
	@Test
	public void updateShouldThrowResouceNotFoundExceptionWhenIdDoNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, clientDTO);
		});
		
		Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
	}
	
	@Test
	public void insertShouldReturnClientDTOWhenInsertNewClient() {
		Assertions.assertDoesNotThrow(() -> {
			service.insert(clientDTO);
		});
		
		Mockito.verify(repository, Mockito.times(1)).save(client);
	}
}
