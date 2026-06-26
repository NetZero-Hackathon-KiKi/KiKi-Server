package com.netzero.service;

import com.netzero.entity.TimelinePost;
import com.netzero.entity.User;
import com.netzero.repository.TimelinePostRepository;
import com.netzero.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;
    private final TimelinePostRepository timelinePostRepository;

    public List<User> getDepartmentRanking(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return userRepository.findByDepartmentAndUniversityOrderByXpDesc(user.getDepartment(), user.getUniversity());
    }

    public List<User> getUniversityRanking(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return userRepository.findByUniversityOrderByXpDesc(user.getUniversity());
    }

    public int getMyRank(Long userId) {
        List<User> ranking = getUniversityRanking(userId);
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getId().equals(userId)) return i + 1;
        }
        return -1;
    }

    public List<TimelinePost> getDepartmentTimeline(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return timelinePostRepository.findByUser_DepartmentAndUser_UniversityOrderByCreatedAtDesc(
                user.getDepartment(), user.getUniversity());
    }
}
