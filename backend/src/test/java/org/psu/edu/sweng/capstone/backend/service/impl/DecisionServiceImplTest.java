package org.psu.edu.sweng.capstone.backend.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.psu.edu.sweng.capstone.backend.dao.BallotDAO;
import org.psu.edu.sweng.capstone.backend.dao.DecisionDAO;
import org.psu.edu.sweng.capstone.backend.dao.DecisionUserDAO;
import org.psu.edu.sweng.capstone.backend.dao.UserDAO;
import org.psu.edu.sweng.capstone.backend.dto.DecisionDTO;
import org.psu.edu.sweng.capstone.backend.dto.ResponseEntity;
import org.psu.edu.sweng.capstone.backend.dto.UserDTO;
import org.psu.edu.sweng.capstone.backend.exception.EntityNotFoundException;
import org.psu.edu.sweng.capstone.backend.model.Ballot;
import org.psu.edu.sweng.capstone.backend.model.Decision;
import org.psu.edu.sweng.capstone.backend.model.DecisionUser;
import org.psu.edu.sweng.capstone.backend.model.User;
import org.psu.edu.sweng.capstone.backend.service.BallotService;

@ExtendWith(MockitoExtension.class)
class DecisionServiceImplTest extends ServiceImplTest {
	
	@Mock
	private UserDAO userDao;
	
	@Mock
	private BallotDAO ballotDao;
	
	@Mock
	private DecisionDAO decisionDao;
	
	@Mock
	private BallotService ballotService;
	
	@Mock
	private DecisionUserDAO decisionUserDao;
		
	@InjectMocks
	private DecisionServiceImpl decisionServiceImpl;

	private Long decisionId = 1337L;
	private User testUser = new User("pop pop", "90210", "Wayne", "Clark", "123imfake@email.gov", new Date(911L));
	private Decision dec = new Decision("Test Decision", "Test Description", testUser);
	private DecisionUser decUser = new DecisionUser(dec, new User("TestUser", "fakepw", "User", "Test", "TestUser@gmail.com", new Date(1337L)));
	private Set<DecisionUser> decisionUsers = new HashSet<>();
		
	@BeforeEach
	void setUp() {       
		// given
		decisionUsers.add(decUser);
		
		dec.setId(decisionId);
		dec.setDecisionUsers(decisionUsers);
	}
		
	@Test
	void getUsers_returnsUsers_whenUsersExist() throws EntityNotFoundException {
		// when
		when(decisionDao.findById(decisionId)).thenReturn(Optional.ofNullable(dec));
		ResponseEntity<UserDTO> response = decisionServiceImpl.getUsers(1337L);
		
		// then
		assertEquals(1, response.getData().size());
		assertEquals("TestUser", response.getData().get(0).getUserName());
	}
	
	@Test
	void getUsers_returnsEmptyList_whenNoUsersExist() throws EntityNotFoundException {
		// when
		when(decisionDao.findById(decisionId)).thenReturn(Optional.ofNullable(null));
	    assertThrows(EntityNotFoundException.class, () -> { decisionServiceImpl.getUsers(1337L); });
	}
	
	@Test
	void getDecision_noDecisionExists() throws EntityNotFoundException {
		when(decisionDao.findById(decisionId)).thenReturn(Optional.ofNullable(null));
	    assertThrows(EntityNotFoundException.class, () -> { decisionServiceImpl.getDecision(decisionId); });
	}
	
	@Test
	void getDecision_decisionExists() throws EntityNotFoundException {
		when(decisionDao.findById(decisionId)).thenReturn(Optional.ofNullable(dec));
		ResponseEntity<DecisionDTO> response = decisionServiceImpl.getDecision(decisionId);

		assertEquals("Test Decision", response.getData().get(0).getName());
	}
	
	@Test
	void updateDecision_noDecisionExists() throws EntityNotFoundException {
		when(decisionDao.findById(decisionId)).thenReturn(Optional.empty());
	    assertThrows(EntityNotFoundException.class, () -> { decisionServiceImpl.updateDecision(decisionId, DecisionDTO.build(dec)); });
    }
	
	@Test
	void updateDecision_decisionExists_hasNullValues_includedUsers() throws EntityNotFoundException {
		// given
		UserDTO userDTO = UserDTO.build(testUser);
		Decision decision = new Decision(null, null, null);
		DecisionDTO decisionDTO = DecisionDTO.build(decision);
		
		decisionDTO.setId(1L);
		decisionDTO.getIncludedUsers().add(userDTO);
		
		// when
		when(decisionDao.findById(decisionId)).thenReturn(Optional.ofNullable(decision));
		when(userDao.findByUserName(userDTO.getUserName())).thenReturn(Optional.ofNullable(testUser));
		ResponseEntity<DecisionDTO> response = decisionServiceImpl.updateDecision(decisionId, decisionDTO);
		
		// then
		assertEquals(200, response.getStatus());
		assertEquals(0, response.getErrors().size());
	}
	
	@Test
	void updateDecision_decisionExists_hasNullValues_includedUserNotFound() throws EntityNotFoundException {
		// given
		UserDTO userDTO = UserDTO.build(testUser);
		Decision decision = new Decision(null, null, null);
		DecisionDTO decisionDTO = DecisionDTO.build(decision);
		
		decisionDTO.setId(1L);
		decisionDTO.getIncludedUsers().add(userDTO);
		
		// when
		when(decisionDao.findById(decisionId)).thenReturn(Optional.ofNullable(decision));
		when(userDao.findByUserName(userDTO.getUserName())).thenReturn(Optional.empty());
		ResponseEntity<DecisionDTO> response = decisionServiceImpl.updateDecision(decisionId, decisionDTO);
		
		// then
		assertEquals(200, response.getStatus());
		assertEquals(0, response.getErrors().size());
	}
	
	@Test
	void updateDecision_decisionExists_hasActualValues() throws EntityNotFoundException {
		// given
		Decision decision = new Decision("Test Decision", "Test Description", testUser);
		decision.getBallots().add(new Ballot(decision, new Date(), new HashSet<>()));
		decision.setId(1L);
		
		DecisionDTO decisionDTO = DecisionDTO.build(decision);
		decisionDTO.setIncludedUsers(null);
		
		// when
		when(decisionDao.findById(decisionId)).thenReturn(Optional.ofNullable(decision));
		ResponseEntity<DecisionDTO> response = decisionServiceImpl.updateDecision(decisionId, decisionDTO);
		
		// then
		assertGenericSuccess(response);
	}
	
	@Test
	void createDecision_hasNoUser() throws EntityNotFoundException {
		when(userDao.findByUserName(testUser.getUserName())).thenReturn(Optional.empty());
	    assertThrows(EntityNotFoundException.class, () -> { decisionServiceImpl.createDecision(DecisionDTO.build(dec)); });
	}
		
	@Test
	void createDecision_hasUser_addsDecisionUsersAndBallot() throws EntityNotFoundException {
		// given
		dec.getDecisionUsers().add(new DecisionUser(dec, testUser));
		dec.getBallots().add(new Ballot(dec, new Date(), new HashSet<>()));
		
		DecisionDTO dto = DecisionDTO.build(dec);
		
		// when
		when(userDao.findByUserName(testUser.getUserName())).thenReturn(Optional.ofNullable(testUser));
		ResponseEntity<DecisionDTO> response = decisionServiceImpl.createDecision(dto);
		
		// then
		assertCreatedSuccess(response);
	}
	
	@Test
	void createDecision_hasUser_noBallot() throws EntityNotFoundException {
		// given
		DecisionDTO dto = DecisionDTO.build(dec);
		
		// when
		when(userDao.findByUserName(testUser.getUserName())).thenReturn(Optional.ofNullable(testUser));
		ResponseEntity<DecisionDTO> response = decisionServiceImpl.createDecision(dto);
		
		// then
		assertCreatedSuccess(response);
	}
	
	@Test
	void deleteDecision_hasNoDecision() throws EntityNotFoundException {
		when(decisionDao.findById(decisionId)).thenReturn(Optional.empty());
	    assertThrows(EntityNotFoundException.class, () -> { decisionServiceImpl.deleteDecision(dec.getId()); });
	}
	
	@Test
	void deleteDecision_decisionExists_noUserDecisions() throws EntityNotFoundException {
		when(decisionDao.findById(decisionId)).thenReturn(Optional.ofNullable(dec));
		ResponseEntity<DecisionDTO> response = decisionServiceImpl.deleteDecision(dec.getId());

		assertGenericSuccess(response);
	}
		
	@Test
	void deleteDecision_decisionExists_withUserDecisions() throws EntityNotFoundException {		
		when(decisionDao.findById(decisionId)).thenReturn(Optional.ofNullable(dec));
		ResponseEntity<DecisionDTO> response = decisionServiceImpl.deleteDecision(dec.getId());

		assertGenericSuccess(response);
	}
}
