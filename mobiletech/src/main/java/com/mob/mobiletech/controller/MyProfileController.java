/**
 * 
 */
package com.mob.mobiletech.controller;

import java.util.List;

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

import com.mob.mobiletech.constants.Constants;
import com.mob.mobiletech.dao.BaseDAO;
import com.mob.mobiletech.domain.TransactionRequest;
import com.mob.mobiletech.domain.User;
import com.mob.mobiletech.util.CommonUtils;


/**
 * @author Prashant
 * 
 */
@Controller
public class MyProfileController {
	
	private final Logger LOGGER= Logger.getLogger(MyProfileController.class);
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
	



	@RequestMapping(value = "/viewProfile", method = RequestMethod.GET)
	   public ModelAndView viewProfile(@RequestParam("userid") Integer userid, ModelMap model) {
	    LOGGER.info("Inside viewProfile  userid :"+userid );
	      List<Object> userList= baseDAO.fetchAll(userid, "userId", User.class.getName());
	      
	      model.addAttribute("access", "read");
			model.addAttribute("menu", "profile");
			
			
		
	      return new ModelAndView("createUser", "user", userList.get(0));
	   }
	
	@RequestMapping(value = "/viewChild", method = RequestMethod.GET)
	   public ModelAndView viewChild(@RequestParam("userid") Integer userid, ModelMap model) {
		LOGGER.info("Enter viewChild userid :"+ userid );
	     
	      List<Object> userList= baseDAO.fetchAll(userid, "prntId", User.class.getName());
	      model.addAttribute("menu", "viewchild");
	      return new ModelAndView("result", "userlst", userList);
	   }
	
	@RequestMapping(value = "/viewRetailer", method = RequestMethod.GET)
	   public ModelAndView viewRetailer(@RequestParam("userid") Integer userid, ModelMap model) {
	     LOGGER.info("Enter viewRetailer userid :"+userid);
	      List<Object> userList= baseDAO.fetchAll(2, "role", User.class.getName());
	      model.addAttribute("menu", "viewRetailer");
	      return new ModelAndView("viewRetailer", "userlst", userList);
	   }
	
	/*@RequestMapping(value = "/editRetailerLoad", method = RequestMethod.GET)
	   public ModelAndView editRetailerLoad(@RequestParam("userid") Integer userid, ModelMap model) {
	     
		List<Object> userList= baseDAO.fetchAll(userid, "userId", User.class.getName());
	      model.addAttribute("menu", "viewRetailer");
	      TransactionRequest txn= new TransactionRequest();
	      txn.setAmt(0);
	      model.addAttribute("txn", txn);
	      return new ModelAndView("edit", "user", userList.get(0));
	   }*/
	
	
	@RequestMapping(value = "/editUserLoad", method = RequestMethod.GET)
	   public ModelAndView editUserLoad(@RequestParam("userid") Integer userid, ModelMap model) {
		LOGGER.info("Enter editUserLoad userid :"+userid);
	      List<Object> userList= baseDAO.fetchAll(userid, "userId", User.class.getName());
	      model.addAttribute("menu", "viewchild");
	      TransactionRequest txn= new TransactionRequest();
	      txn.setAmt(0);
	      model.addAttribute("txn", txn);
	      return new ModelAndView("edit", "user", userList.get(0));
	   }
	
	
	
	@RequestMapping(value = "/editUserSubmit", method = RequestMethod.POST)
	public ModelAndView editUserSubmit(@ModelAttribute("SpringWeb")User user,@RequestParam("amt") int amt, HttpServletRequest request,ModelMap model) 
	{
		
		User userLoggedin= (User)request.getSession().getAttribute("user");
		userLoggedin=(User)baseDAO.fetchAll(userLoggedin.getUserId(), "userId", User.class.getName()).get(0);// temperory added for cuncurrent threads
		LOGGER.info("Enter editUserSubmit userLoggedin :"+userLoggedin.getUserId()+" edituser :"+user.getUserId() +"amount "+amt);
		if(amt>0)
		{
			TransactionRequest txn = new TransactionRequest();
			txn.setAmt(amt);
			if (userLoggedin.getRole()==0)
			{
				userLoggedin.recharge(txn.getAmt().floatValue(), 0.0F);//changed as per new requirement
				txn.setPreviousBal(user.getAvailableBalance());
				user.setAvailableBalance(user.getAvailableBalance()+txn.getAmt());
				userLoggedin.setBalance(userLoggedin.getBalance()+txn.getAmt());
				txn.setCurrentBal(user.getAvailableBalance());
				txn.setApprovalDate(CommonUtils.getSystemDate());
				txn.setRequestedBy(user.getUserId());
				txn.setRequestedTo(userLoggedin.getUserId());
				txn.setRequesterName(user.getFirstName()+" "+user.getLastName());
				txn.setCreateDate(CommonUtils.getSystemDate());
				txn.setApproverRemark("A Recharge of Amount "+txn.getAmt() + " is done on your account");
				
				baseDAO.saveOrUpdate(userLoggedin);
				baseDAO.saveOrUpdate(txn);
			}
			else
			{
				if(userLoggedin.getAvailableBalance()<=txn.getAmt())
				{
					LOGGER.info("Error not enough balance");
					model.addAttribute("menu", "viewchild");
					
				      model.addAttribute("txn", txn);
					model.addAttribute("error", "003");
					return new ModelAndView("edit","user",user);
				}
				else{
					userLoggedin.recharge(txn.getAmt().floatValue(), 0.0F);
					txn.setPreviousBal(user.getAvailableBalance());
					user.setAvailableBalance(user.getAvailableBalance()+txn.getAmt());
					txn.setCurrentBal(user.getAvailableBalance());
					//user.setAvailableBalance(user.getAvailableBalance()+txn.getAmt());
					txn.setApprovalDate(CommonUtils.getSystemDate());
					txn.setRequestedBy(user.getUserId());
					txn.setRequestedTo(userLoggedin.getUserId());
					txn.setRequesterName(user.getFirstName()+" "+user.getLastName());
					txn.setCreateDate(CommonUtils.getSystemDate());
					txn.setApproverRemark("A Recharge of Amount "+txn.getAmt() + " is done on your account");
					baseDAO.saveOrUpdate(userLoggedin);
					baseDAO.saveOrUpdate(txn);
				}
			}
		}
		LOGGER.info("loggedin"+userLoggedin.getUserId()+ "user updated balance "+userLoggedin.getAvailableBalance());
		LOGGER.info("edit "+user.getUserId()+ "user updated balance "+user.getAvailableBalance());
		baseDAO.update(user);
		
		request.setAttribute("user", userLoggedin);
		model.addAttribute("message", "User has been updated successfully :)");
		model.addAttribute("menu", "viewchild");
		TransactionRequest txn= new TransactionRequest();
	      txn.setAmt(0);
	      model.addAttribute("txn", txn);
		
	return new ModelAndView("edit","user",user);}
		
	

}
