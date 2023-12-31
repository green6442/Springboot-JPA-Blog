package com.cos.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;

// 스프링이 컴포넌트 스캔을 통해 Bean에 등록해줌. IoC를 해준다. 메모리에 대신 띄워준다.
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Transactional // 여러가지 서비스를 사용할 경우 전부 성공해야 commit
	public void 회원가입(User user) {
		String rawPassword = user.getPassword();
		String encPassword = encoder.encode(rawPassword);
		user.setPassword(encPassword);
		user.setRole(RoleType.USER);
		userRepository.save(user);
	}

	@Transactional
	public void 회원수정(User user) {
		// 수정 시에는 영속선 컨텍스트 User 오브젝트를 영속화 시키고, 영속화된 User 오브젝트를 수정
		// SELECT 하여 User 오브젝트를 DB로부터 가져오는 이유는 영속화를 하기 위해.
		// 영속화된 오브젝트를 변경하면 자동으로 DB에 UPDATE 쿼리 실행
		User persistance = userRepository.findById(user.getId()).orElseThrow(()->{
					return new IllegalArgumentException("회원 찾기 실패");
		});
		String rawPassword = user.getPassword();
		String encPassword = encoder.encode(rawPassword);
		persistance.setPassword(encPassword);
		persistance.setEmail(user.getEmail());
		// 회원수정 함수 종료 = 서비스 종료 = 트랜잭션 종료 = 자동 COMMIT
		// 영속화된 persistance 객체의 변화가 감지되면 더디체킹 : update 쿼리 실행

	}
	
	@Transactional (readOnly = true)
	public User  회원찾기(String username) {
		User user = userRepository.findByUsername(username).orElseGet(()->{
			return new User();
		});
		return user;
	}

	/*
	 * @Transactional (readOnly = true) // select 할 때 트랜잭션 시작, 서비스 종료시에 트랜잭션 종료
	 * (정합성) public User 로그인(User user) { return
	 * userRepository.findByUsernameAndPassword(user.getUsername(),
	 * user.getPassword()); }
	 */

}
