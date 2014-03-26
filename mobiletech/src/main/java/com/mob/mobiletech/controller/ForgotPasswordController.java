/**
 * 
 */
package com.mob.mobiletech.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mob.mobiletech.dao.BaseDAO;
import com.mob.mobiletech.domain.TransactionRequest;
import com.mob.mobiletech.domain.User;
import com.mob.mobiletech.form.ForgotPasswordForm;
import com.mob.mobiletech.form.UpdatePasswordForm;
import com.mob.mobiletech.services.MailMail;
import com.mob.mobiletech.util.CommonUtils;

/**
 * @author Prashant
 *
 */
@Controller
public class ForgotPasswordController {

	private final Logger LOGGER= Logger.getLogger(ForgotPasswordController.class);
	@Autowired
	BaseDAO baseDAO;
	@Autowired
	//private MailSender mailSender;
	private MailMail mailMail;
	public void setMailMail(MailMail mailMail) {
		this.mailMail = mailMail;
	}
	
	/**
	 * @return the baseDAO
	 */
	public BaseDAO getBaseDAO() {
		return baseDAO;
	}

	/**
	 * @param baseDAO the baseDAO to set
	 */
	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}
	
	@RequestMapping(value = "/forgotPasswordLoad", method = RequestMethod.GET)
	public ModelAndView forgotPasswordLoad()
	{
		return new ModelAndView("forgotPassword","forgot",new ForgotPasswordForm());
	}
	
	@RequestMapping(value = "/forgotPasswordSubmit", method = RequestMethod.POST)
	public ModelAndView forgotPasswordSubmit(@ModelAttribute("SpringWeb")ForgotPasswordForm forgotPasswordForm, HttpServletRequest request,ModelMap model) 
	{
		
		User user=(User)baseDAO.getUser(forgotPasswordForm.getUserName());
		
		
		if(user==null)
		{
			model.addAttribute("error", "008");
			return new ModelAndView("forgotPassword","forgot",forgotPasswordForm);
			
		}else
		if(!user.getEmailId().equals(forgotPasswordForm.getEmailId()))
		{
			model.addAttribute("error", "008");
			return new ModelAndView("forgotPassword","forgot",forgotPasswordForm);
			
		}
		else
		{
			LOGGER.info("Enter forgotPasswordSubmit userLoggedin :"+user.getUserId());
			//String password=user.getPassword();
		//	baseDAO.update(userLoggedin);
			String text= "Hi " +user.getFirstName()+
	                   ",\n\nThe password of your MobileTech account reset successfully!!\n\nPlease find your account details:"+
	                   "\n\nUser Id: " + user.getUserId() +
	                   "\nUser Name: " + user.getUserName() + 
	                   "\nPassword: " +user.getPassword()+
	 		           "\n\nThanks for being with MobileTech."+
	                   "\n\nRegards,\nSales Team,\nMobileTech";
	   
	      mailMail.sendMail("welcome@mobiletech.co.in",
	      		user.getEmailId(),
	  		   "MobileTech: Password Reset", 
	  		   text);
			model.addAttribute("message", "Your Password is reset and sent your email id");
			return new ModelAndView("forgotPassword","forgot",forgotPasswordForm);
		}
		
		}
	
}
