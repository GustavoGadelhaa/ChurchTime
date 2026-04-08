package com.church.backend.identity.service;

import com.church.backend.identity.entity.Group;
import com.church.backend.identity.entity.User;
import com.church.backend.identity.repository.GroupRepository;
import com.church.backend.identity.repository.UserRepository;
import com.church.backend.shared.security.AccessPolicy;
import com.church.backend.shared.security.CurrentUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChurchService churchService;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private AccessPolicy accessPolicy;

    @InjectMocks
    private GroupService groupService;


    @Nested
    class TestsGroupService{

        @Test
        @DisplayName("Deve adiconar user em grupo")
        void deveAddUserEmGrupo() {

            Group group2 = Group.builder().id(5L).name("teste2").build();
            User user = User.builder().id(10L).name("joao").build();

            when(userRepository.findById(any())).thenReturn(Optional.of(user));
            when(groupRepository.findById(4L)).thenReturn(Optional.of(group2));

            groupService.addUserToGroup(4L, 10L);
            System.out.println();


        }
    }

}