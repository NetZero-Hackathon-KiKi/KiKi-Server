package com.netzero.service;

import com.netzero.dto.response.RankEntryResponse;
import com.netzero.dto.response.RankingResponse;
import com.netzero.entity.User;
import com.netzero.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public RankingResponse getUniversityRanking(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<User> rankPage = userRepository.findByUniversityOrderByCurrentXpDesc(
                user.getUniversity(), PageRequest.of(page, size));

        int startRank = page * size + 1;
        AtomicInteger rank = new AtomicInteger(startRank);
        List<RankEntryResponse> rankings = rankPage.getContent().stream()
                .map(u -> toRankEntry(u, rank.getAndIncrement()))
                .collect(Collectors.toList());

        RankEntryResponse myRanking = findMyRanking(user);

        return RankingResponse.builder()
                .rankings(rankings)
                .myRanking(myRanking)
                .rankChange(myRanking.getRankChange())
                .build();
    }

    @Transactional(readOnly = true)
    public RankingResponse getDepartmentRanking(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<User> rankPage = userRepository.findByUniversityAndDepartmentOrderByCurrentXpDesc(
                user.getUniversity(), user.getDepartment(), PageRequest.of(page, size));

        int startRank = page * size + 1;
        AtomicInteger rank = new AtomicInteger(startRank);
        List<RankEntryResponse> rankings = rankPage.getContent().stream()
                .map(u -> toRankEntry(u, rank.getAndIncrement()))
                .collect(Collectors.toList());

        RankEntryResponse myRanking = findMyDepartmentRanking(user);

        return RankingResponse.builder()
                .rankings(rankings)
                .myRanking(myRanking)
                .rankChange(myRanking.getRankChange())
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getMyRanking(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<User> universityUsers = userRepository.findByUniversityOrderByCurrentXpDesc(
                user.getUniversity(), PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        int universityRank = findRankInList(universityUsers, userId);

        List<User> departmentUsers = userRepository.findByUniversityAndDepartmentOrderByCurrentXpDesc(
                user.getUniversity(), user.getDepartment(), PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        int departmentRank = findRankInList(departmentUsers, userId);

        return Map.of(
                "universityRank", universityRank,
                "departmentRank", departmentRank,
                "greenPoint", user.getGreenPoint(),
                "nickname", user.getNickname(),
                "department", user.getDepartment() != null ? user.getDepartment() : "",
                "university", user.getUniversity() != null ? user.getUniversity() : "",
                "rankChange", 0
        );
    }

    private RankEntryResponse toRankEntry(User user, int rank) {
        return RankEntryResponse.builder()
                .rank(rank)
                .userId(user.getId())
                .nickname(user.getNickname())
                .university(user.getUniversity())
                .department(user.getDepartment())
                .profileImageUrl(user.getProfileImageUrl())
                .greenPoint(user.getGreenPoint())
                .rankChange(0)
                .build();
    }

    private RankEntryResponse findMyRanking(User user) {
        List<User> allUsers = userRepository.findByUniversityOrderByCurrentXpDesc(
                user.getUniversity(), PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        int rank = findRankInList(allUsers, user.getId());
        return toRankEntry(user, rank);
    }

    private RankEntryResponse findMyDepartmentRanking(User user) {
        List<User> deptUsers = userRepository.findByUniversityAndDepartmentOrderByCurrentXpDesc(
                user.getUniversity(), user.getDepartment(), PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        int rank = findRankInList(deptUsers, user.getId());
        return toRankEntry(user, rank);
    }

    private int findRankInList(List<User> users, Long userId) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(userId)) return i + 1;
        }
        return 0;
    }
}
