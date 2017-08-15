package com.spittr.image.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.spittr.authorization.annotation.Authorization;
import com.spittr.authorization.annotation.AutoCurrentUser;
import com.spittr.config.StaticConfig;
import com.spittr.image.core.ImageIssues;
import com.spittr.image.core.MessageImageService;
import com.spittr.image.model.MessageImage;
import com.spittr.message.core.MessageService;
import com.spittr.model.ReturnModel;
import com.spittr.user.model.User;


@RestController
@RequestMapping(value="/api/image")
public class ImageController {
	
	@Autowired
	@Qualifier("messageImageServiceImpl")
	private MessageImageService messageImageService;
	
	@Autowired
	@Qualifier("messageServiceImpl")
	private MessageService messageService;
	
	@RequestMapping(value="/create", method=RequestMethod.POST)
	@ResponseStatus(value=HttpStatus.OK)
	@Authorization
	public ReturnModel base64Image(
			@AutoCurrentUser User user,
			@RequestParam("image") String image,
			HttpServletRequest request
			) throws IOException{
		
		String imageDirPath = request.getSession().getServletContext().getRealPath(StaticConfig.DEFAULT_IMAGE_DIRECTORY);
		String appRootDir = request.getServletContext().getContextPath();
		
		String webPath = messageImageService.saveImageFromBase64(imageDirPath, image);
		MessageImage messageImage = messageImageService.get(webPath);

		ImageIssues.formatImagePath(messageImage, appRootDir);
		return ReturnModel.SUCCESS(messageImage);
	}
	
}
