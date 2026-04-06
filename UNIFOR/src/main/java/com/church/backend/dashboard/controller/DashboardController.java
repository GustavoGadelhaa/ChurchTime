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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para estatísticas do dashboard
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Estatísticas para o dashboard")
public class DashboardController {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final EventRepository eventRepository;
    private final PresenceRepository presenceRepository;

    /**
     * Retorna as estatísticas resumidas para o dashboard do usuário logado.
     * 
     * @return ResponseEntity contendo as estatísticas do dashboard
     */
    @GetMapping("/stats")
    @Operation(summary = "Obter estatísticas do dashboard", 
               description = "Retorna as estatísticas resumidas para o dashboard do usuário logado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        // Obter o usuário logado
        User currentUser = currentUserService.requireCurrent();
        
        // Obter a igreja do usuário (através do grupo do usuário)
        Long churchId = currentUser.getGroup() != null ? 
                        currentUser.getGroup().getChurch().getId() : 
                        null;
        
        // Se o usuário não estiver em nenhum grupo, tentamos buscar pela igreja ativa
        if (churchId == null) {
            // Este caso seria tratado adequadamente dependendo das regras de negócio
            // Por enquanto, retornamos zeros ou fazemos uma busca alternativa
            churchId = getDefaultChurchId();
        }
        
        // Calcular as estatísticas
        Map<String, Object> stats = new HashMap<>();
        
        // Total de grupos da igreja
        stats.put("totalGroups", groupRepository.countByChurchIdAndActiveTrue(churchId));
        
        // Eventos abertos (status=OPEN)
        stats.put("openEvents", eventRepository.countByChurchIdAndStatus(churchId, EventStatus.OPEN));
        
        // Membros ativos da igreja (usuários ativos que pertencem à igreja)
        stats.put("activeMembers", userRepository.countByChurchIdAndActiveTrue(churchId));
        
        // Check-ins de hoje
        stats.put("todayCheckins", presenceRepository.countTodayCheckinsByChurchId(churchId));
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Obtém o ID da igreja padrão (usado quando usuário não tem grupo).
     * Implementação simples - em um cenário real, isso dependeria das regras de negócio.
     */
    private Long getDefaultChurchId() {
        return userRepository.findFirstActiveChurchId();
    }

    @GetMapping("/my-group")
    @jakarta.transaction.Transactional
    @Operation(summary = "Obter meu grupo",
               description = "Retorna informações do grupo ao qual o usuário logado está vinculado, incluindo contagem de membros e eventos ativos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do grupo retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não tem grupo vinculado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
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