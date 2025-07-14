package acc.firewatch.member.repository;

import acc.firewatch.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByPhoneNum(String phoneNum);
    Optional<Member> findByPhoneNum(String phoneNum);
}
