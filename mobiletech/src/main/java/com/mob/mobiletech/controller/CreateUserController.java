/**
 * 
 */
package com.mob.mobiletech.controller;



import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
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
import com.mob.mobiletech.services.MailMail;
import com.mob.mobiletech.util.CommonUtils;




/**
 * @author Prashant
 *
 */
@Controller
public class CreateUserController  {
	private final Logger LOGGER= Logger.getLogger(CreateUserController.class);
	
	//private LoginValidation loginValidation;
	
	/*public CreateUserController()
	{
		setCommandClass(User.class);
		setCommandName("user");
		setFormView("admin/createUser");
		
		
	}*/
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

	@RequestMapping(value = "/createUserLoad", method = RequestMethod.GET)
	public ModelAndView createUserLoad(@RequestParam("role") int role,@RequestParam("prntid") int prntId, ModelMap model)
	{
		LOGGER.info("enter createUserLoad role ::"+role+"userId ::"+prntId);
	User user=new User();
	user.setRole(role);
	user.setPrntId(prntId);
	model.addAttribute("menu", "create");
	return new ModelAndView("createUser","user",user);
	}
	
	@RequestMapping(value = "/createUserSubmit", method = RequestMethod.POST)
	public ModelAndView createUserSubmit(@ModelAttribute("SpringWeb")User user, HttpServletRequest request,ModelMap model) 
	{
		User userLoggedin= (User)request.getSession().getAttribute("user");
		LOGGER.info("enter createUserSubmit userid ::"+userLoggedin.getUserId());
		/*if(userLoggedin.getCommission()<=user.getCommission())
		{
			model.addAttribute("error","002");
			return new ModelAndView("createUser","user",user);
		}*/
		userLoggedin.setAvailableBalance(userLoggedin.getAvailableBalance()-user.getAvailableBalance());
		userLoggedin.setUsedBalance(userLoggedin.getUsedBalance()+user.getAvailableBalance());
		user.setUsedBalance(0.0F);
		request.getSession().setAttribute("user", userLoggedin);
		user.setRegisteredOn(CommonUtils.getSystemDate());
		
		//user.setLoginStatus((Constants.ROLE_RETAILER+"").equals(user.getRole())?Constants.LOGIN_STATUS_PENDING_FOR_APPROVAL:Constants.LOGIN_STATUS_ACTIVE);
		user.setLoginStatus(Constants.LOGIN_STATUS_ACTIVE);
		if(userLoggedin.getRole()==1)
		{
			TransactionRequest tnx= new TransactionRequest();
			tnx.setRequestedBy(userLoggedin.getUserId());
			tnx.setRequesterName(user.getUserName());
			tnx.setRequestedTo(userLoggedin.getPrntId());
			LOGGER.info("userid ::"+userLoggedin.getUserId() +"Please approve the Retailer's login with username "+user.getUserName()+"and name "+user.getFirstName()+" "+user.getLastName());
			tnx.setRequesterRemark("Please approve the Retailer's login with username "+user.getUserName()+"and name "+user.getFirstName()+" "+user.getLastName());
			tnx.setCreateDate(CommonUtils.getSystemDate());
			user.setLoginStatus(Constants.LOGIN_STATUS_PENDING_FOR_APPROVAL);
			baseDAO.saveOrUpdate(tnx);
		}
		else
		{
			userLoggedin.setBalance(userLoggedin.getBalance()+user.getAvailableBalance());
		}
		baseDAO.saveOrUpdate(userLoggedin);
		baseDAO.saveOrUpdate(user);
		
		 //sending mail to the newly created user with their detials
		if(user.getLoginStatus().equals(Constants.LOGIN_STATUS_ACTIVE)){
			{
				String text= "Hi " +user.getFirstName()+
		                   ",\n\nWelcome to Mobiletech Family!!\n\nPlease find your account details:"+
		                   "\n\nUser Id: " + user.getUserId() +
		                   "\nUser Name: " + user.getUserName() + 
		                   "\nPassword: " +user.getPassword()+
		 		           "\n\nThanks for being with MobileTech."+
		                   "\n\nRegards,\nSales Team,\nMobileTech";
		   
		      mailMail.sendMail("welcome@mobiletech.co.in",
		      		user.getEmailId(),
		  		   "Welcome to MobileTech", 
		  		   text);
			}	
		}
		
		model.addAttribute("message", "User "+user.getFirstName()+" "+user.getLastName()+" Is created with user Id "+ user.getUserId());
		model.addAttribute("access", "read");
		model.addAttribute("menu", "profile");
		
		
	return new ModelAndView("createUser","user",userLoggedin);
	}
	
	
	@RequestMapping(value = "/approveRequestLoad", method = RequestMethod.GET)
	public ModelAndView approveRequestLoad(@RequestParam("reqid") int reqid,@RequestParam("reqtype") String reqtype, @RequestParam("username") String username,  ModelMap model)
	{
		LOGGER.info("reqid :: "+reqid+" reqtype ::"+reqtype+" username"+username);
		User user=new User();
		if(reqtype.length()==0)
		{
			user= (User)baseDAO.fetchAll(username, "userName", User.class.getName()).get(0);
		}
		
		 model.addAttribute("menu", "viewrequest");
	      TransactionRequest txn= new TransactionRequest();
	      txn.setAmt(0);
	      model.addAttribute("txn", txn);
	      return new ModelAndView("edit", "user", user);
	}
		
	

@RequestMapping(value = "/approveRequestsubmit", method = RequestMethod.POST)
public ModelAndView approveRequestsubmit(@ModelAttribute("SpringWeb")User user, HttpServletRequest request,ModelMap model)
{
	//User user=new User();
	LOGGER.info("approveRequestsubmit for userid "+user.getUserId());
	user.setLoginStatus(Constants.LOGIN_STATUS_ACTIVE);
	baseDAO.saveOrUpdate(user);
	
	 model.addAttribute("menu", "viewrequest");
      TransactionRequest txn= new TransactionRequest();
      txn.setAmt(0);
      model.addAttribute("approved", "approved");
      
      model.addAttribute("txn", txn);
      String text= "Hi " +user.getFirstName()+
              ",\n\nWelcome to Mobiletech Family!!\n\nPlease find your account details:"+
              "\n\nUser Id: " + user.getUserId() +
              "\nUser Name: " + user.getUserName() + 
              "\nPassword: " +user.getPassword()+
              "\nDistributer Name:"+user.getPrntName()+
              "\nDistributer Contact No:"+user.getPrntPhoneNo()+
	          "\n\nThanks for being with MobileTech."+
              "\n\nRegards,\nSales Team,\nMobileTech";
    //sending mail to the newly created user with their detials
      mailMail.sendMail("welcome@mobiletech.co.in",
      		user.getEmailId(),
  		   "Welcome to MobileTech", 
  		   text);
      
      return new ModelAndView("edit", "user", user);
}
}


	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	/*@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		User user=(User)command;
		this.getLoginValidation().saveUser(user);
		return new ModelAndView("admin/success");
		
	}*/



	
	
	


