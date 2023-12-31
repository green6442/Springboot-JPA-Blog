package com.cos.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blog.dto.ReplySaveRequestDto;
import com.cos.blog.model.Board;
import com.cos.blog.model.Reply;
import com.cos.blog.model.User;
import com.cos.blog.repository.BoardRepository;
import com.cos.blog.repository.ReplyRepository;
import com.cos.blog.repository.UserRepository;

// 스프링이 컴포넌트 스캔을 통해 Bean에 등록해줌. IoC를 해준다. 메모리에 대신 띄워준다.
@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private ReplyRepository replyRepository;

	@Transactional // 여러가지 서비스를 사용할 경우 전부 성공해야 commit
	public void 글쓰기(Board board, User user) {
		board.setCount(0);
		board.setUser(user);
		boardRepository.save(board);
	}

	@Transactional (readOnly = true)
	public Page<Board> 글목록(Pageable pageable) {
		return boardRepository.findAll(pageable);
	}

	@Transactional (readOnly = true)
	public Board 글상세보기(int id) {
		return boardRepository.findById(id)
				.orElseThrow(()->{
					return new IllegalArgumentException("글 상세보기 실패 : 아이디를 찾을 수 없습니다.");
				});
		
	}

	@Transactional 
	public void 삭제하기(int id) {
		boardRepository.deleteById(id);
	}

	@Transactional 
	public void 글수정하기(int id, Board requestBoard) {
		Board board = boardRepository.findById(id)
				.orElseThrow(()->{
					return new IllegalArgumentException("글 수정 실패 : 아이디를 찾을 수 없습니다.");
				}); // 영속화 완료
		board.setTitle(requestBoard.getTitle());
		board.setContent(requestBoard.getContent());
		// 해당 함수로 종료시 (Service가 종료될 때) 트랜잭션이 종료됨
		// 더티 체킹 ( DB로 자동 업데이트)
	}

	@Transactional
	public void 댓글쓰기(ReplySaveRequestDto replySaveRequestDto) {
		int result = replyRepository.mSave(replySaveRequestDto.getUserId(), replySaveRequestDto.getBoardId(), replySaveRequestDto.getContent());
		System.out.println("BoardService : "+result);
		/*
		 * User user = userRepository.findById(replySaveRequestDto.getUserId())
		 * .orElseThrow(()->{ return new
		 * IllegalArgumentException("댓글 쓰기 실패 : 아이디를 찾을 수 없습니다."); }); // 영속화 완료
		 * 
		 * Board board = boardRepository.findById(replySaveRequestDto.getBoardId())
		 * .orElseThrow(()->{ return new
		 * IllegalArgumentException("댓글 쓰기 실패 : 게시글을 찾을 수 없습니다."); }); // 영속화 완료
		 * 
		 * Reply reply = Reply.builder() .user(user) .board(board)
		 * .content(replySaveRequestDto.getContent()) .build();
		 */

		/*
		 * Reply reply = new Reply(); reply.update(user, board,
		 * replySaveRequestDto.getContent());
		 */
	}

	@Transactional
	public void 댓글삭제(int replyId) {
		replyRepository.deleteById(replyId);
	}

}
