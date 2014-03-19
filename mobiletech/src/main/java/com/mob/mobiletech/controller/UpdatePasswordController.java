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
import com.mob.mobiletech.form.UpdatePasswordForm;
import com.mob.mobiletech.util.CommonUtils;

/**
 * @author Prashant
 *
 */
@Controller
public class UpdatePasswordController {

	private final Logger LOGGER= Logger.getLogger(UpdatePasswordController.class);
	@Autowired
	BaseDAO baseDAO;
	
	
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
	
	@RequestMapping(value = "/updatePasswordLoad", method = RequestMethod.GET)
	public ModelAndView updatePasswordLoad()
	{
		return new ModelAndView("updatePassword","update",new UpdatePasswordForm());
	}
	
	@RequestMapping(value = "/updatePasswordSubmit", method = RequestMethod.POST)
	public ModelAndView updatePasswordSubmit(@ModelAttribute("SpringWeb")UpdatePasswordForm updatePasswordForm, HttpServletRequest request,ModelMap model) 
	{
		
		User userLoggedin= (User)request.getSession().getAttribute("user");
		
		LOGGER.info("Enter updatePasswordSubmit userLoggedin :"+userLoggedin.getUserId());
		if(!userLoggedin.getPassword().equals(updatePasswordForm.getOldPassword()))
		{
			model.addAttribute("error", "005");
			return new ModelAndView("updatePassword","update",updatePasswordForm);
			
		}
		else if(!updatePasswordForm.getNewPassword().equals(updatePasswordForm.getConfirmPassword()))
		{
			model.addAttribute("error","006");
			return new ModelAndView("updatePassword","update",updatePasswordForm);
		}
		else
		{
			userLoggedin.setPassword(updatePasswordForm.getNewPassword());
			baseDAO.update(userLoggedin);
			model.addAttribute("message", "Your Password Updated Successfully");
			return new ModelAndView("updatePassword","update",updatePasswordForm);
		}
		
		}
	
}
