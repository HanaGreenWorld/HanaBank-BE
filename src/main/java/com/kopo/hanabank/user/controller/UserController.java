package com.kopo.hanabank.user.controller;

import com.kopo.hanabank.common.dto.ApiResponse;
import com.kopo.hanabank.user.domain.User;
import com.kopo.hanabank.user.dto.UserCreateRequest;
import com.kopo.hanabank.user.dto.UserResponse;
import com.kopo.hanabank.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "사용자 관리", description = "사용자 관련 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getName(),
                request.getBirthDate(),
                request.getAddress()
        );
        return ApiResponse.success("사용자가 성공적으로 생성되었습니다.", new UserResponse(user));
    }

    @Operation(summary = "사용자 조회", description = "ID로 사용자를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ApiResponse.success(new UserResponse(user));
    }

    @Operation(summary = "사용자명으로 조회", description = "사용자명으로 사용자를 조회합니다.")
    @GetMapping("/username/{username}")
    public ApiResponse<UserResponse> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ApiResponse.success(new UserResponse(user));
    }

    @Operation(summary = "이메일로 조회", description = "이메일로 사용자를 조회합니다.")
    @GetMapping("/email/{email}")
    public ApiResponse<UserResponse> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ApiResponse.success(new UserResponse(user));
    }

    @Operation(summary = "전체 사용자 조회", description = "모든 사용자를 조회합니다.")
    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        return ApiResponse.success(userResponses);
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id, 
                                              @RequestParam String name, 
                                              @RequestParam String address) {
        User user = userService.updateUser(id, name, address);
        return ApiResponse.success("사용자 정보가 성공적으로 수정되었습니다.", new UserResponse(user));
    }

    @Operation(summary = "사용자 비활성화", description = "사용자를 비활성화합니다.")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ApiResponse.success("사용자가 성공적으로 비활성화되었습니다.", null);
    }
}












