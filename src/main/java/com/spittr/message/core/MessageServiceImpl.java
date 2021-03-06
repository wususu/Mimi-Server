package com.spittr.message.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spittr.config.StaticConfig;
import com.spittr.image.dao.MessageImageDao;
import com.spittr.image.model.MessageImage;
import com.spittr.message.dao.MessageDao;
import com.spittr.message.exception.MessageNotFoundException;
import com.spittr.message.model.Message;
import com.spittr.tools.Mapper;
import com.spittr.tools.page.Page;
import com.spittr.user.dao.UserRelationshipDao;
import com.spittr.user.model.User;
import com.spittr.user.model.UserRelationship;

import java.util.List;

import static com.spittr.core.JSONConstants.*;
import static com.spittr.config.StaticConfig.*;

@Service
public class MessageServiceImpl implements MessageService{

	@Autowired
	@Qualifier("messageDaoImpl")
	private MessageDao messageDao;
	
	@Autowired
	@Qualifier("commentServiceImpl")
	private CommentService commentService;
	
	@Autowired
	@Qualifier("likeeServiceImpl")
	private LikeeService likeeService;
	
	@Autowired
	@Qualifier("userRelationshipDaoImpl")
	private UserRelationshipDao userRelationshipDao;
	
	@Autowired
	@Qualifier("messageImageDaoImpl")
	private MessageImageDao messageImageDao;
	
	@Override
	@Transactional
	public void create(Message message) {
		// TODO Auto-generated method stub

		save(message);
	}
	
	@Override
	@Transactional
	public void createWhichImage(Message message, Set<MessageImage> imageSet){
		create(message);
		for (MessageImage messageImage : imageSet) {
			messageImage.setMessage(message);
			messageImage.setMid(message.getMid());
			messageImageDao.update(messageImage);
		}
		message.setMessageImageSet(imageSet);
	}

	@Override
	public void save(Message message) {
		// TODO Auto-generated method stub
		messageDao.save(message);
	}
	
	@Override
	public Message get(Long mid) {
		// TODO Auto-generated method stub
		return messageDao.get(Message.class, mid);
	}

	@Override
	public Message need(Long mid) {
		// TODO Auto-generated method stub
		Message message = get(mid);
		
		if (message == null) 
			throw new MessageNotFoundException(mid);
		
		MessageIssues.checkIsDelete(message);
		
		message = MessageIssues.generateFakeMessage(message);
		
		return message;
	}
	
	
	@Override
	@Transactional
	public void delete(Message message) {
		// TODO Auto-generated method stub
		if (message == null) 
			throw new NullPointerException();
		
		if (message.isDelete()) 
			throw new MessageNotFoundException();
		
		message.setDelete(true);
		message.setTmDelete(new Date());
		
		messageDao.update(message);
		
		commentService.deleteUnderMessage(message);
	}
	
	@Override
	public void adcNextCommentVal(Message message){
		if (message == null) 
			throw new NullPointerException();
		
		message.setCommentNextVal( message.getCommentNextVal()+1 );
		messageDao.update(message);
	}

	@Override
	public void adcCommentCount(Message message) {
		// TODO Auto-generated method stub
		if (message == null) 
			throw new NullPointerException();
		
		message.setCommentCount( (message.getCommentCount() + 1) );
		messageDao.update(message);
	}

	@Override
	public void decCommentCount(Message message) {
		// TODO Auto-generated method stub
		if (message == null) 
			throw new NullPointerException();
		
		message.setCommentCount( (message.getCommentCount() - 1) );
		messageDao.update(message);
	}
	
	@Deprecated
	@Override
	public Map<String, Object> getMessageByPageNumber(Integer pageNumber){
		Long items = messageDao.count();
		Page page = Page.newInstance(items);
		page.setPage(pageNumber);
		
		List<Message> messageList = messageDao.get(page);
		messageList = MessageIssues.generateFakeMessageList(messageList);
		
		page.setItemInThisPage(messageList.size());
		
		Map<String, Object> map = getMap();
		map.put(PAGE, page);
		map.put(MESSAGE_LIST, messageList);

		return map;
	}

	@Deprecated
	@Override
	public Map<String, Object> getLocaleMessageByPageNumber(Long lid, Integer pageNumber) {
		// TODO Auto-generated method stub
		Long items = messageDao.coutByLid(lid);
		Page page = Page.newInstance(items);
		page.setPage(pageNumber);
		
		List<Message> messageList = messageDao.getByLid(page, lid);
		messageList = MessageIssues.generateFakeMessageList(messageList);

		page.setItemInThisPage(messageList.size());
		
		Map<String, Object> map = getMap();
		map.put(PAGE, page);
		map.put(MESSAGE_LIST, messageList);
		return map;
	}
	
	// 判断是否登录状态
	private List<Message> judgeLikee(List<Message> messages, User user) {
		// TODO Auto-generated method stub
		if (user == null) 
			return messages;
		
		if (messages.size() == 0) 
			return messages;
		
		for (int i=0; i<messages.size(); i++) 
			likeeService.generateLikee(messages.get(i), user);
				
		return messages;
	}

	// 所有before time message
	@Override
	public Map<String, Object> beforeTimeMessages(Date tmbefore, User currentUser){
		List< Message> messages = messageDao.getBeforeTime(tmbefore, ITEM_PER_PAGE);
		
		messages = judgeLikee(MessageIssues.generateFakeMessageList(messages), currentUser);
		
		return Mapper.newInstance(getMap()).
		add(BEFORE_TIME, tmbefore).
		add(NUM_PER_PAGE, ITEM_PER_PAGE).
		add(NUM_THIS_PAGE, messages.size()).
		add(MESSAGE_LIST, messages)
		.getMap();
	}

	// 所有after time message
	@Override
	public Map<String, Object> afterTimeMessages(Date tmafter, User currentUser){
		List< Message> messages = messageDao.getAfterTime(tmafter, ITEM_PER_PAGE);
		
		messages = judgeLikee(MessageIssues.generateFakeMessageList(messages), currentUser);
		
		return Mapper.newInstance(getMap()).
		add(AFTER_TIME, tmafter).
		add(NUM_PER_PAGE, ITEM_PER_PAGE).
		add(NUM_THIS_PAGE, messages.size()).
		add(MESSAGE_LIST, messages)
		.getMap();
	}

	// 某一地点的before time message
	@Override
	public Map<String, Object> beforeTimeMessages(Date tmbefore, User currentUser, Long lid) {
		// TODO Auto-generated method stub
		List<Message> messages = messageDao.getBeforeTime(tmbefore, lid, ITEM_PER_PAGE);
			
		messages = judgeLikee(MessageIssues.generateFakeMessageList(messages), currentUser);
		
		return Mapper.newInstance(getMap()).
		add(BEFORE_TIME, tmbefore).
		add(NUM_PER_PAGE, ITEM_PER_PAGE).
		add(NUM_THIS_PAGE, messages.size()).
		add(MESSAGE_LIST, messages)
		.getMap();
	}

	// 某一地点的after time message
	@Override
	public Map<String, Object> afterTimeMessages(Date tmafter, User currentUser, Long lid) {
		// TODO Auto-generated method stub
		List<Message> messages = messageDao.getAfterTime(tmafter, lid, ITEM_PER_PAGE);
		
		messages = judgeLikee(MessageIssues.generateFakeMessageList(messages), currentUser);

		return Mapper.newInstance(getMap()).
		add(AFTER_TIME, tmafter).
		add(NUM_PER_PAGE, ITEM_PER_PAGE).
		add(NUM_THIS_PAGE, messages.size()).
		add(MESSAGE_LIST, messages)
		.getMap();
	}

	// 某一用户的message
	@Override
	public Map<String, Object> userMessages(Date tmbefore, User objectUser, User currentUser) {
		// TODO Auto-generated method stub
		
		List<Message> messages = null;
		
		// 是否查看自己的message
		if (objectUser.equals(currentUser)){ 
			messages = messageDao.getByUid(objectUser.getUid(), tmbefore, ITEM_PER_PAGE);
			messages = MessageIssues.generateFakeMessageList(messages); 
		}else {
			messages = messageDao.getNotFakeByUid(objectUser.getUid(), tmbefore, ITEM_PER_PAGE);
		}
		
		messages = judgeLikee(messages, currentUser);
		
		return Mapper.newInstance(getMap()).
		add(BEFORE_TIME, tmbefore).
		add(NUM_PER_PAGE, ITEM_PER_PAGE).
		add(NUM_THIS_PAGE, messages.size()).
		add(MESSAGE_LIST, messages)
		.getMap();
	}

	
	@Override
	public Map<String, Object> myAttentionMessages(Date tmbefore, User mainUser) {
		// TODO Auto-generated method stub
		Set<Long> uids = new HashSet<>();
		List<UserRelationship> userRelationships = userRelationshipDao.getMainUserRelationships(mainUser);
		List<Message> messages = new ArrayList<>();
		if (userRelationships.size() != 0) {
		
			for (UserRelationship userRelationship : userRelationships) {
				uids.add(userRelationship.getObjectUser().getUid());
			}
			messages = messageDao.getByUids(uids, tmbefore, ITEM_PER_PAGE);
			
			messages = judgeLikee(MessageIssues.removeFakeMessage(MessageIssues.generateFakeMessageList(messages)), mainUser);
			
		}
		return Mapper.newInstance(getMap())
				.add(BEFORE_TIME, tmbefore)
				.add(NUM_PER_PAGE, ITEM_PER_PAGE)
				.add(NUM_THIS_PAGE, messages.size())
				.add(MESSAGE_LIST, messages)
				.getMap();
	}


}
