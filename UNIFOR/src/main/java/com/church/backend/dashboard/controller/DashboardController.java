package com.church.backend.dashboard.controller;

import com.church.backend.attendance.entity.EventStatus;
import com.church.backend.attendance.repository.EventRepository;
import com.church.backend.attendance.repository.PresenceRepository;
import com.church.backend.identity.dto.GroupDtos.MyGroupResponse;
import com.church.backend.identity.entity.Group;
import com.church.backend.identity.entity.User;
import com.church.backend.identity.repository.GroupRepository;
import com.church.backend.identity.repository.UserRepository;
import com.church.backend.shared.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final EventRepository eventRepository;
    private final PresenceRepository presenceRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        User currentUser = currentUserService.requireCurrent();
        
        Map<String, Object> stats = new HashMap<>();
        
        if (currentUser.getGroup() == null) {
            stats.put("message", "Usuário sem grupo associado");
            stats.put("test", Map.of("name", "No group", "members", 0));
            return ResponseEntity.ok(stats);
        }
        
        Long churchId = currentUser.getGroup().getChurch().getId();
        Long groupId = currentUser.getGroup().getId();
        String role = String.valueOf(currentUser.getRole());
        
        stats.put("yourGroup", currentUser.getGroup().getName());
        stats.put("role", role);
        
        if ("LEADER".equals(role)) {
            stats.put("totalGroups", groupRepository.countByChurchIdAndActiveTrue(churchId));
            stats.put("openEvents", eventRepository.countByChurchIdAndStatus(churchId, EventStatus.OPEN));
            stats.put("activeMembers", userRepository.countByChurchIdAndActiveTrue(churchId));
            stats.put("todayCheckins", presenceRepository.countTodayCheckinsByChurchId(churchId));
            stats.put("groupMembers", userRepository.countByGroupIdAndActiveTrue(groupId));
            stats.put("groupOpenEvents", eventRepository.countByGroupIdAndStatus(groupId, EventStatus.OPEN));
        } else {
            stats.put("myEvents", eventRepository.countByGroupIdAndStatus(groupId, EventStatus.OPEN));
            stats.put("myGroupMembers", userRepository.countByGroupIdAndActiveTrue(groupId));
        }
        
        return ResponseEntity.ok(stats);
    }
    
    private Long getDefaultChurchId() {
        return userRepository.findFirstActiveChurchId();
    }

    @GetMapping("/my-group")
    @jakarta.transaction.Transactional
    public ResponseEntity<MyGroupResponse> getMyGroup() {
        User currentUser = currentUserService.requireCurrent();

        if (currentUser.getGroup() == null) {
            return ResponseEntity.notFound().build();
        }

        Group group = currentUser.getGroup();

        int memberCount = userRepository.countByGroupIdAndActiveTrue(group.getId());
        int activeEvents = eventRepository.countByGroupIdAndStatus(group.getId(), EventStatus.OPEN);

        String leaderName = group.getLeader() != null ? group.getLeader().getName() : null;

        MyGroupResponse response = new MyGroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                leaderName,
                memberCount,
                activeEvents,
                group.isActive()
        );

        return ResponseEntity.ok(response);
    }
}
