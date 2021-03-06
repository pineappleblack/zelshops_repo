package com.ipovselite;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HomeController implements Controller {
	@Autowired
	private ShopDAO shopDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	//private List<Shop> shopList=new ArrayList<Shop>();
	//private User currentUser;
	//private String currentAccess;
	/*public String getCurrentAccess() {
		return currentAccess;
	}
	public void setCurrentAccess(String currentAccess) {
		this.currentAccess = currentAccess;
	}
	public User getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	private  boolean isFirstVisit=true;
	@Bean
	public boolean isFirstVisit() {
		return isFirstVisit;
	}
	public void setFirstVisit(boolean isFirstVisit) {
		this.isFirstVisit = isFirstVisit;
	}
	public List<Shop> getShopList() {
		return shopList;
	}
	public void setShopList(List<Shop> shopList) {
		this.shopList=shopList;
	}*/
	private static final Logger logger = Logger.getLogger(HomeController.class);
	
	@RequestMapping(value="/search",method={RequestMethod.GET})
	public String home(ModelMap model,HttpSession session) {
		SearchParameters searchForm = new SearchParameters();
		model.addAttribute("searchForm", searchForm);
		List<String> specList = new ArrayList<String>();
		specList.add("Еда");
		specList.add("Здоровье");
		specList.add("Одежда");
		specList.add("Электроника");
		specList.add("Спорт");
		model.addAttribute("specList", specList);
		java.util.Enumeration<String> sessEnum = session

				.getAttributeNames();
				  while (sessEnum.hasMoreElements()) {

				String s = sessEnum.nextElement();

				System.out.print(s);

				System.out.println("==" + session.getAttribute(s));

				  }
				

		if (session.getAttribute("isFirstVisit")==null)
			session.setAttribute("isFirstVisit",true);
		if (session.getAttribute("currentUser")==null)
			session.setAttribute("currentUser",null);
		if (session.getAttribute("currentAccess")==null)
			session.setAttribute("currentAccess",null);
		if (session.getAttribute("shopList")==null)
			session.setAttribute("shopList",null); 
		model.addAttribute("isFirstVisit",session.getAttribute("isFirstVisit"));
		model.addAttribute("currentUser",session.getAttribute("currentUser"));
		model.addAttribute("currentAccess",session.getAttribute("currentAccess"));
		logger.debug("Added specList to the model.");
		List<Shop> shopList=(ArrayList<Shop>)session.getAttribute("shopList");
		if (shopList!=null)
			if (!shopList.isEmpty()) {
				List<Shop> tempList=shopList;
				model.addAttribute("shopList", tempList);
				logger.debug("Added shopList to the model.");
				for (Shop s : tempList)
					logger.debug("Name: "+s.getName());
			}
			else {
				logger.error("shopList is empty!");
			}
		return "search_result1";
		
	}
	
	@RequestMapping(value="/search",method={RequestMethod.POST})
	public String getSearchResult(@ModelAttribute("searchForm") SearchParameters searchParam, Map<String,Object> model,HttpSession session) {
		if (searchParam!=null) {
			List<Shop> shopList = shopDAO.search(searchParam);
			logger.debug("Assigned shopList:");
			for (Shop s : shopList) {
				logger.debug("Name: "+s.getName());
			}
			session.setAttribute("shopList", shopList);
		}
		else
			logger.error("searchParam is null!");
		boolean isFirstVisit=false;
		session.setAttribute("isFirstVisit", isFirstVisit);
		return "redirect:/search";
		
	}
	@RequestMapping(value="/login",method={RequestMethod.GET})
	public String loginForm(HttpServletRequest request, HttpSession session, ModelMap model) {
		User user = new User();
		model.addAttribute("loginForm", user);
		String msg=request.getParameter("msg");
		model.addAttribute("msg",msg);
		session.setAttribute("isFirstVisit", true);
		session.setAttribute("shopList", null);
		return "login_form";
		
	}
	
	@RequestMapping(value="/login",method={RequestMethod.POST})
	public String getLoginResult(@ModelAttribute("loginForm") User user,Map<String,Object> model,HttpSession session) {
		User currentUser=null;
		String currentAccess=null;
		if ((currentUser=userDAO.isValid(user.getUsername(),user.getPassword()))!=null) {
			switch (currentUser.getAccess()) {
			case Admin: currentAccess="Администратор";break;
			case Moderator: currentAccess="Модератор";break;
			default: break;
			}
			session.setAttribute("currentUser", currentUser);
			session.setAttribute("currentAccess", currentAccess);
			session.setAttribute("shopList", null);
			session.setAttribute("isFirstVisit", true);
			return "redirect:search";
		}
		else
			return "redirect:login?msg=fail";
	}
	@RequestMapping(value="/logout",method={RequestMethod.GET})
	public String logoutGet(Map<String,Object> model,HttpSession session) {
		
		User currentUser=null;
		session.setAttribute("currentUser", currentUser);
		session.setAttribute("currentAccess", null);
		session.setAttribute("shopList", null);
		session.setAttribute("isFirstVisit", true);
		return "redirect:/search";
	}
	@RequestMapping(value="/logout",method={RequestMethod.POST})
	public String logoutPost(Map<String,Object> model) {
		
		//currentUser=null;
		return "redirect:/search";
	}
	@RequestMapping(value="/addshop",method={RequestMethod.GET})
	public String addShopGet(HttpServletRequest request, HttpSession session, ModelMap model) {
		Shop shop = new Shop();
		String msg = request.getParameter("msg");
		List<String> specList = new ArrayList<String>();
		specList.add("Еда");
		specList.add("Здоровье");
		specList.add("Одежда");
		specList.add("Электроника");
		specList.add("Спорт");
		model.addAttribute("specList", specList);
		model.addAttribute("msg", msg);
		model.addAttribute("shopForm", shop);
		session.setAttribute("shopList", null);
		session.setAttribute("isFirstVisit", true);
		return "add_shop";
	}
	@RequestMapping(value="/addshop",method={RequestMethod.POST})
	public String addShopPost(@ModelAttribute("shopForm") Shop shop,Map<String,Object> model,HttpSession session) {
		if (shop.getName().equals("") || shop.getSite().equals("") || shop.getSite().equals("") || shop.getSite().equals(""))
			return "redirect:addshop?msg=fail";
		//currentUser=null;
		shopDAO.addShop(shop);
		return "redirect:/search";
	}
	@RequestMapping(value="/newshops",method={RequestMethod.GET})
	public String newShopsGet(HttpServletRequest request, HttpSession session, ModelMap model) {
		
		List<Shop> shopList=shopDAO.findByStatus(0);
		model.addAttribute("shopList", shopList);
		session.setAttribute("shopList", null);
		session.setAttribute("isFirstVisit", true);
		return "new_shops";
	}
	@RequestMapping(value="/makeactive",method={RequestMethod.GET})
	public String makeActive(HttpServletRequest request, HttpSession session, ModelMap model) {
		shopDAO.updateColumn("status", 3,new Integer(request.getParameter("id")));
		session.setAttribute("shopList", null);
		session.setAttribute("isFirstVisit", true);
		return "redirect:search";
		
	}
	@RequestMapping(value="/makeinactive",method={RequestMethod.GET})
	public String makeInactive(HttpServletRequest request, HttpSession session, ModelMap model) {
		shopDAO.updateColumn("status", 0,new Integer(request.getParameter("id")));
		session.setAttribute("shopList", null);
		session.setAttribute("isFirstVisit", true);
		return "redirect:search";
		
	}
	@RequestMapping(value="/deleteshop",method={RequestMethod.GET})
	public String deleteShop(HttpServletRequest request, HttpSession session, ModelMap model) {
		shopDAO.delete(new Integer(request.getParameter("id")));
		session.setAttribute("shopList", null);
		session.setAttribute("isFirstVisit", true);
		return "redirect:search";
		
	}
	@RequestMapping(value="/checkshops",method={RequestMethod.GET})
	public String checkShopsGet(HttpServletRequest request, HttpSession session, ModelMap model) {
		
		List<Shop> shopList=shopDAO.findByStatus(2);
		model.addAttribute("shopList", shopList);
		session.setAttribute("shopList", null);
		session.setAttribute("isFirstVisit", true);
		return "check_shops";
	}
	@RequestMapping(value="/updateshop",method={RequestMethod.GET})
	public String updateShopGet(HttpServletRequest request, HttpSession session, ModelMap model) {
		Shop shop = shopDAO.get(new Integer(request.getParameter("id")));
		System.out.println("----------------------SHOPID IN UPDATESHOPGET: "+shop.getId());
		String msg = request.getParameter("msg");
		model.addAttribute("updateShopForm", shop);
		List<String> specList = new ArrayList<String>();
		specList.add("Еда");
		specList.add("Здоровье");
		specList.add("Одежда");
		specList.add("Электроника");
		specList.add("Спорт");
		model.addAttribute("specList", specList);
		model.addAttribute("msg", msg);
		
		session.setAttribute("changeShopId", shop.getId());
		session.setAttribute("shopList", null);
		session.setAttribute("isFirstVisit", true);
		return "update_shop";
	}
	@RequestMapping(value="/updateshop",method={RequestMethod.POST})
	public String updateShopPost(@ModelAttribute("updateShopForm") Shop shop,Map<String,Object> model,HttpSession session) {
		Integer id= (Integer)session.getAttribute("changeShopId");
		if (shop.getName().equals("") || shop.getSite().equals("") || shop.getSite().equals("") || shop.getSite().equals(""))
			return "redirect:updateshop?id="+id.intValue()+"msg=fail";
		//currentUser=null;
		System.out.println("----------------------SHOPID IN UPDATESHOPPOST: "+shop.getId());
		if (!shop.getName().equals(""))
			shopDAO.updateColumn("name", shop.getName(),id.intValue());
		if (!shop.getSite().equals("") )
			shopDAO.updateColumn("site", shop.getSite(),id.intValue());
		if (!shop.getAddress().equals("") )
			shopDAO.updateColumn("address", shop.getAddress(), id.intValue());
		if (!shop.getTelephone().equals("") )
			shopDAO.updateColumn("telephone", shop.getTelephone(), id.intValue());
		shopDAO.updateColumn("spec", shop.getSpec(), id.intValue());
		return "redirect:/search";
	}
	@RequestMapping(value="/checkshop",method={RequestMethod.GET})
	public String checkShopGet(HttpServletRequest request, HttpSession session, ModelMap model) {
		shopDAO.updateColumn("status", 2,new Integer(request.getParameter("id")));
		session.setAttribute("shopList", null);
		session.setAttribute("isFirstVisit", true);
		return "redirect:search";
		
	}
	public Class<? extends Annotation> annotationType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String value() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
