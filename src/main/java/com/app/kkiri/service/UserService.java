package com.app.kkiri.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.kkiri.domain.dto.UserDTO;
import com.app.kkiri.domain.dto.response.BookmarkedPostListResponseDTO;
import com.app.kkiri.domain.dto.response.BookmarkedPostResponseDTO;
import com.app.kkiri.domain.dto.response.MyCommentListResponseDTO;
import com.app.kkiri.domain.dto.response.MyCommentResponseDTO;
import com.app.kkiri.domain.dto.response.MyPostListResponseDTO;
import com.app.kkiri.domain.dto.response.MyPostResponseDTO;
import com.app.kkiri.domain.dto.response.UserMypageResponseDTO;
import com.app.kkiri.domain.dto.response.UserProfileResponseDTO;
import com.app.kkiri.domain.dto.response.UserResponseDTO;
import com.app.kkiri.domain.vo.CommentVO;
import com.app.kkiri.domain.vo.PostVO;
import com.app.kkiri.domain.vo.SpaceUserVO;
import com.app.kkiri.repository.CommentsDAO;
import com.app.kkiri.repository.PostBookmarksDAO;
import com.app.kkiri.repository.PostImgsDAO;
import com.app.kkiri.repository.PostsDAO;
import com.app.kkiri.repository.SpaceUsersDAO;
import com.app.kkiri.repository.SpacesDAO;
import com.app.kkiri.repository.UsersDAO;
import com.app.kkiri.security.enums.UserStatus;
import com.app.kkiri.security.jwt.JwtTokenProvider;
import com.app.kkiri.security.oAuth2Login.AuthenticatedOAuth2User;
import com.app.kkiri.security.oAuth2Login.CustomOAuth2User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UsersDAO usersDAO;
	private final PostsDAO postsDAO;
	private final CommentsDAO commentsDAO;
	private final SpaceUsersDAO spaceUsersDAO;
	private final PostBookmarksDAO postBookmarksDAO;
	private final SpacesDAO spacesDAO;
	private final PostImgsDAO postImgsDAO;
	private final JwtTokenProvider jwtTokenProvider;
	private final FileService fileService;
	private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	// userId 를 사용하여 회원 조회
	@Transactional(rollbackFor = Exception.class)
	public UserResponseDTO search(Long userId) {
		return usersDAO.findById(userId);
	}

	// userId를 사용하여 마이페이지 내 정보 조회
	@Transactional(rollbackFor = Exception.class)
	public UserMypageResponseDTO searchMypage(Long userId) {
		UserResponseDTO userResponseDTO = usersDAO.findById(userId);

		UserMypageResponseDTO userMypageResponseDTO = UserMypageResponseDTO.builder()
				.socialType(userResponseDTO.getSocialType())
				.email(userResponseDTO.getUserEmail())
				.build();
		LOGGER.info("[searchMypage()] userMypageResponseDTO : {}", userMypageResponseDTO);

		return userMypageResponseDTO;
	}

	// 회원 가입 및 로그인
	@Transactional(rollbackFor = Exception.class)
	public AuthenticatedOAuth2User register(CustomOAuth2User customOAuth2User) throws OAuth2AuthenticationException {
		LOGGER.info("[register()] param customOAuth2User.getName() : {}", customOAuth2User.getName());
		LOGGER.info("[register()] param customOAuth2User.getEmail() : {}", customOAuth2User.getEmail());

		// 토큰에 이메일 데이터가 없는 경우
		String userEmail = customOAuth2User.getEmail();
		if(userEmail == null) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN), "토큰에 이메일 주소가 포함되어 있지 않습니다");
		}

		UserResponseDTO selectedUser = usersDAO.findByUserEmail(userEmail);
		String socialType = customOAuth2User.getRegistrationId();

		// 동일한 이메일 주소로 다른 소셜 로그인을 시도하는 경우
		if(selectedUser != null && userEmail.equals(selectedUser.getUserEmail()) && !socialType.equals(selectedUser.getSocialType())) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT), "이미 가입된 이메일 주소입니다");
		}

		UserResponseDTO userResponseDTO = null;

		if(selectedUser == null) { // 신규 회원인 경우 회원 가입을 한다
			Long nextUserId = usersDAO.findNextUserId();
			String accessToken = jwtTokenProvider.createAccessToken(nextUserId);
			String refreshToken = jwtTokenProvider.createRefreshToken(nextUserId);

			UserDTO userDTO = UserDTO.builder()
				.userId(nextUserId)
				.socialType(customOAuth2User.getRegistrationId())
				.userStatus(UserStatus.USER.getUserStatus())
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.userEmail(userEmail)
				.build();

			usersDAO.save(userDTO);

			userResponseDTO = usersDAO.findRecentUser();
			LOGGER.info("register() [if 신규 회원인 경우 result] userResponseDTO : {}", userResponseDTO);
		} else { // 기존 회원인 경우
			userResponseDTO = usersDAO.findByUserEmail(userEmail);
			LOGGER.info("register() [if 기존 회원인 경우 start] userResponseDTO : {}", userResponseDTO);

			// 리프레쉬 토큰이 만료되어 다시 소셜 로그인을 하는 경우
			if(!jwtTokenProvider.validateToken(userResponseDTO.getRefreshToken())) {
				Long selectedUserId = userResponseDTO.getUserId();

				String newAccessToken = jwtTokenProvider.createAccessToken(selectedUserId);
				String newRefreshToken = jwtTokenProvider.createRefreshToken(selectedUserId);
				usersDAO.setTokens(selectedUserId, newAccessToken, newRefreshToken);

				userResponseDTO = usersDAO.findById(selectedUserId);
				LOGGER.info("register() [if 기존 회원인 경우 end] userResponseDTO : {}", userResponseDTO);
			}
		}

		AuthenticatedOAuth2User authenticatedOAuth2User = AuthenticatedOAuth2User.builder()
			.userId(userResponseDTO.getUserId())
			.socialType(userResponseDTO.getSocialType())
			.userStatus(userResponseDTO.getUserStatus())
			.accessToken(userResponseDTO.getAccessToken())
			.refreshToken(userResponseDTO.getRefreshToken())
			.userEmail(userResponseDTO.getUserEmail())
			.build();
		LOGGER.info("register() returned value authenticatedOAuth2User : {}", authenticatedOAuth2User);

		return authenticatedOAuth2User;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateAccessToken(String reissuedAccessToken) throws AuthenticationException {
		LOGGER.info("[updateAccessToken()] param reissuedAccessToken : {}", reissuedAccessToken);

		Long userId = jwtTokenProvider.getUserIdByToken(reissuedAccessToken);

		usersDAO.setAccessToken(userId, reissuedAccessToken);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteUser(Long userId) {

		usersDAO.deleteUser(userId);
	}


	// 사용자가 북마크한 게시글 정보를 조회
	@Transactional
	public BookmarkedPostListResponseDTO bookmarkList(Long userId, int page) {
		LOGGER.info("[bookmarkList()] param page : {}", page);

		int itemLength = 10;
		Long startIndex = Long.valueOf((page - 1) * itemLength);
		List<PostVO> selectedPosts = postsDAO.findBookmarkedPostsByUserIdAndPage(userId, startIndex);
		Long total = postBookmarksDAO.countByUserId(userId);

		List<BookmarkedPostResponseDTO> postResponseDTOS = new ArrayList<>();
		for(PostVO postVO : selectedPosts) {
			BookmarkedPostResponseDTO postResponseDTO = BookmarkedPostResponseDTO.builder()
				.postId(postVO.getPostId())
				.postTitle(postVO.getPostTitle())
				.postCommentCount(commentsDAO.getTotal(postVO.getPostId()))
				.postCreatedAt(postVO.getPostRegisterDate())
				.postWriterName(spaceUsersDAO.findUserNicknameByPostId(postVO.getPostId()))
				.build();

			postResponseDTOS.add(postResponseDTO);
		}

		BookmarkedPostListResponseDTO bookmarkedPostListResponseDTO = BookmarkedPostListResponseDTO.builder()
			.bookMarkList(postResponseDTOS)
			.page(page)
			.total(total)
			.itemLength(itemLength)
			.build();
		LOGGER.info("[findBookmarkedPostsByUserIdAndPage()] postBookmarkResponseDTO : {}",
			bookmarkedPostListResponseDTO);

		return bookmarkedPostListResponseDTO;
	}

	// 사용자가 등록한 게시글을 페이징하여 조회한다.
	@Transactional(rollbackFor = Exception.class)
	public MyPostListResponseDTO myPostList(Long userId, int page) {
		LOGGER.info("[myPostList()] param page : {}", page);

		int itemLength = 10;
		Long startIndex = Long.valueOf ((page - 1) * itemLength);
		Long total = postsDAO.countByUserId(userId);

		List<MyPostResponseDTO> myPostList = new ArrayList<>();

		// 페이징 처리 된 사용자가 작성한 게시글 리스트
		List<PostVO> selectedPostList = postsDAO.findByUserId(userId, startIndex);
		for(PostVO postVO : selectedPostList) {

			// 게시글에 맨션된 사용자 리스트
			List<SpaceUserVO> selectedUserList = spaceUsersDAO.findByPostIdAndSpaceId(postVO.getPostId(), postVO.getSpaceId());
			List<UserProfileResponseDTO> selectedUserProfileList = new ArrayList<>();
			selectedUserList.stream().forEach(spaceUserVO -> {
				UserProfileResponseDTO userProfileResponseDTO = UserProfileResponseDTO.builder()
					.userId(spaceUserVO.getUserId())
					.userName(spaceUserVO.getUserNickname())
					.imgUrl(fileService.getFileUrl(spaceUserVO.getProfileImgPath()))
					.build();

				selectedUserProfileList.add(userProfileResponseDTO);
			});

			MyPostResponseDTO myPostResponseDTO = MyPostResponseDTO.builder()
				.spaceId(postVO.getSpaceId())
				.postId(postVO.getPostId())
				.postTitle(postVO.getPostTitle())
				.postCreatedAt(postVO.getPostRegisterDate())
				.postCommentCount(commentsDAO.getTotal(postVO.getPostId()))
				.selectedUsers(selectedUserProfileList)
				.build();

			myPostList.add(myPostResponseDTO);
		}

		MyPostListResponseDTO myPostListResponseDTO = MyPostListResponseDTO.builder()
			.myPostList(myPostList)
			.page(page)
			.total(total)
			.itemLength(itemLength)
			.build();
		LOGGER.info("[myPostList()] myPostListResponseDTO : {}", myPostListResponseDTO);

		return myPostListResponseDTO;
	}


	// 사용자가 작성한 댓글을 페이징하여 조회한다.
	@Transactional
	public MyCommentListResponseDTO myCommentList(Long userId, int page) {
		LOGGER.info("[myCommentList()] param page : {}", page);

		int itemLength = 4;
		Long total = commentsDAO.countByUserId(userId);
		Long startIndex = Long.valueOf ((page - 1) * itemLength);

		List<MyCommentResponseDTO> myCommentList = new ArrayList<>();

		List<CommentVO> selectedComments = commentsDAO.findByUserIdAndStartIndex(userId, startIndex);
		for(CommentVO commentVO : selectedComments) {
			Long postId = commentVO.getPostId();
			PostVO postVO = postsDAO.findById(postId);
			Long spaceId = postVO.getSpaceId();

			MyCommentResponseDTO myCommentResponseDTO = MyCommentResponseDTO.builder()
				.postId(postId)
				.postTitle(postVO.getPostTitle())
				.postContent(postVO.getPostContent())
				.postCreateDate(postVO.getPostRegisterDate())
				.spaceTitle(spacesDAO.findBySpaceId(spaceId).getSpaceName())
				.mainImgUrl(fileService.getFileUrl(postImgsDAO.findByPostId(postId)))
				.commentId(commentVO.getCommentId())
				.commentContent(commentVO.getCommentContent())
				.commentCreateDate(commentVO.getCommentRegisterDate())
				.build();

			myCommentList.add(myCommentResponseDTO);
		}

		MyCommentListResponseDTO myCommentListResponseDTO = MyCommentListResponseDTO.builder()
			.myCommentList(myCommentList)
			.page(page)
			.total(total)
			.itemLength(itemLength)
			.build();
		LOGGER.info("[myCommentList()] myCommentListResponseDTO : {}", myCommentListResponseDTO);

		return myCommentListResponseDTO;
	}
}