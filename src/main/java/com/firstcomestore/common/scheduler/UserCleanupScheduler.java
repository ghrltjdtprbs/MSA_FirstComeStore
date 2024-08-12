package com.firstcomestore.common.scheduler;

import com.firstcomestore.domain.user.entity.User;
import com.firstcomestore.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void deleteOldSoftDeletedUsers() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<User> usersToDelete = userRepository.findAllByDeletedAtBefore(oneYearAgo);

        if (!usersToDelete.isEmpty()) {
            userRepository.deleteAll(usersToDelete);
            log.info("총 {}명의 사용자가 데이터베이스에서 삭제되었습니다.", usersToDelete.size());
        } else {
            log.info("삭제할 사용자가 없습니다.");
        }
    }
}
