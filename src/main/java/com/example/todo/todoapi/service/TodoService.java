package com.example.todo.todoapi.service;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.repository.TodoRepository;
import com.example.todo.userapi.entity.Role;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoListResponseDTO create(
            final TodoCreateRequestDTO requestDTO,
            final String userId) {

        // 이제는 할 일 등록은 회원만 할 수 있도록 셋팅하기 때문에
        // toEntity 의 매개값으로 User 엔터티도 함께 전달해야 합니다. -> userId로 회원 엔터티를 조회헤야함.
        User user = getUser(userId);

        // 권한에 따른 글쓰기 제한 처리
        // 일반 회원이 일정을 5개 초과해서 작성하면 예외를 발생.
        if (user.getRole() == Role.COMMON && todoRepository.countByUser(user) >= 5) {
            throw new IllegalArgumentException("일반회원은 5개까지만 등록 가능합니다.");
        }

        todoRepository.save(requestDTO.toEntity(user));
        log.info("할 일 저장완료! 제목: {}", requestDTO.getTitle());
        // 할 일 저장이 끝나면 목록을 불러오는데, 지금까지는 그낭 전부 다 같고 왔어도 된다.
        // 이제는 회원별로 할 일을 등록하기 때문에, 방금 할일을 추가한 그 회원의 목록을 가져와야한다.
        return findAll(userId);
    }

    

    // 할 일 목록 가져오기
    public TodoListResponseDTO findAll(String userId) {
        // 로그인 한 유저의 정보를 데이터베이스에서 조회
        User user = getUser(userId);

        List<Todo> entityList = todoRepository.findAllByUser(user);

        List<TodoDetailResponseDTO> dtoList = entityList.stream()
                //.map(entity -> new TodoDetailResponseDTO(entity))
                .map(TodoDetailResponseDTO::new)// 람다 참조식으로 변경한거 위에랑도 같음
                .collect(Collectors.toList());

        return TodoListResponseDTO.builder()
                .todos(dtoList)
                .build();

    }

    public TodoListResponseDTO delete(final String todoId, String userId) { // 서비스 단에서 매개변수 값 변경 못하도록 final 선언(엄격하게 하려면~)


        todoRepository.findById(todoId).orElseThrow(
                () -> {
                    log.error("id가 존재하지 않아 삭제에 실패했습니다 - ID: {}", todoId);
                    throw new RuntimeException("id가 존재하지 않아 삭제에 실패 했습니다.");
                }
        );
        todoRepository.deleteById(todoId);


        return findAll(userId);
    }

    public TodoListResponseDTO update(final TodoModifyRequestDTO requestDTO, String userId) {
        Optional<Todo> targetEntity = todoRepository.findById(requestDTO.getId());

        targetEntity.ifPresent(todo -> {   // 해당 아이디에 맞는 엔터티가 존재한다면
            todo.setDone(requestDTO.isDone()); //  done값을 넣어주고

            todoRepository.save(todo);

        });
        return findAll(userId);
    }

    // 유저 아이디 조회 추출한 메서드
    private User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );
        return user;
    }
}















