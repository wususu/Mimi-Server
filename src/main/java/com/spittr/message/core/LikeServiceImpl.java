package com.spittr.message.core;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spittr.core.JSONConstants.*;
import com.spittr.message.dao.LikeDao;
import com.spittr.message.dao.MessageDao;
import com.spittr.message.exception.LikeCountErrorException;
import com.spittr.message.model.Like;
import com.spittr.message.model.Message;
import com.spittr.user.model.User;

@Service
public class LikeServiceImpl implements LikeService{

	@Autowired
	@Qualifier("likeDaoImpl")
	private LikeDao likeDao;
	
	@Autowired
	@Qualifier("messageDaoImpl")
	private MessageDao messageDao;
	

	@Override
	public void create(Like entity) {
		// TODO Auto-generated method stub
		likeDao.create(entity);
	}
	
	@Override
	public Like get(long mid, long uid) {
		// TODO Auto-generated method stub
		return likeDao.get(mid, uid);
	}

	@Override
	public Like get(Message message, User user) {
		// TODO Auto-generated method stub
		return likeDao.get(message, user);
	}

	@Override
	@Transactional
	public Map<String, Object> like(Like like) {
		// TODO Auto-generated method stub

		Map<String, Object> result = getMap();
		
		if (like == null) {
			throw new NullPointerException();
		}
		
		if (like.isLike() == true) 
			result.put(LIKE_COUNT,like.getMessage().getLikeCount());
		
		
		if (like.isLike() == false){
			Message message = like.getMessage();
			message.setLikeCount(message.getLikeCount() + 1);
			messageDao.update(message);
			like.setLike(true);
			likeDao.update(like);
			result.put(LIKE_COUNT, message.getLikeCount());
		}
		result.put(ISLIKE, like.isLike());

		return result;
	}

	@Override
	@Transactional
	public Map<String, Object> dislike(Like like) {
		// TODO Auto-generated method stub
		Map<String, Object> result = getMap();
		
		if (like == null) {
			throw new NullPointerException();
		}
		
		if (like.isLike() == false) 
			result.put(LIKE_COUNT,like.getMessage().getLikeCount());

		
		if (like.isLike() == true) {
			Message message = like.getMessage();
			if (message.getLikeCount() < 1) {
				throw new LikeCountErrorException();
			}
			message.setLikeCount( message.getLikeCount() - 1);
			messageDao.update(message);
			like.setLike(false);
			likeDao.update(like);
			result.put(LIKE_COUNT, message.getLikeCount());
		}
		
		result.put(ISLIKE, like.isLike());
		
		return result;
	}

}
