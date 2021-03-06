package org.packt.spring.boot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.packt.spring.boot.model.data.Employee;
import org.packt.spring.boot.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;

@Controller
public class DataController {
	
	private static Logger logger = LoggerFactory.getLogger(DataController.class);
	
	@Autowired
	private EmployeeService employeeServiceImpl;
	
	@RequestMapping("/web/nonreact.html")
	@ResponseBody
	public String normalAction(){
		 Employee emp = null;
		try {
			
		  	 logger.info("controller#normalAction task started.");
			 System.out.println("controller#sync task started. Thread: " +
                     Thread.currentThread()
                           .getName());
			Thread.sleep(10000);
			  emp = employeeServiceImpl.readEmployee(15).get();
              System.out.println(emp.getLastName());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return emp.getLastName();
	}
	
	//@Async
	@RequestMapping(value="/web/react.json",produces ="application/json",method = RequestMethod.GET,headers = {"Accept=text/xml, application/json"})
	@ResponseBody
	public Callable<Employee> asynchAction(){
		Callable<Employee> task = new Callable<Employee>() {
			
			
            @Override
            public Employee call () throws Exception {
            	logger.info("controller#asynchAction task started.");
                System.out.println("controller#async task started. Thread: " +
                                                       Thread.currentThread()
                                                             .getName());
                Thread.sleep(5000);
                Employee emp = employeeServiceImpl.readEmployee(15).get();
                System.out.println(emp.getLastName());
                return emp;
            }
        };
		return task;
	}
	
	@RequestMapping("/web/deferred.html/{id}")
	@ResponseBody
	public DeferredResult<String> square( @PathVariable("id") Integer id) {
	    final DeferredResult<String> deferredResult = new DeferredResult<>();
	    CompletableFuture.supplyAsync(()->{
	    		try {
	    			logger.info("controller#square task started.");
					return employeeServiceImpl.readEmployee(id).get().getFirstName();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
	    }).thenAccept((msg)->{
	    	deferredResult.setResult(msg);
	    });
	   /*
	    deferredResult.onCompletion(() ->{
	        try {
				Thread.sleep(10000);
				 Employee emp = employeeServiceImpl.readEmployee(14).get();
				 deferredResult.setErrorResult("Trial");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.out.println("controller#deferred task started. Thread: " +
                    Thread.currentThread()
                          .getName());
	    });
	    */
	    return deferredResult;
	    
	    
	}
	
	
	@RequestMapping("/web/reactLisEmps.html")
	@ResponseBody
	public Callable<String> listEmpsAsync(){
			Callable<String> task = new Callable<String>() {
			
			
            @Override
            public String call () throws Exception {
            	List<String> names = new ArrayList<>();
            	logger.info("controller#listEmpsAsync task started.");
                System.out.println("controller#async task started. Thread: " +
                          Thread.currentThread()
                                     .getName());
                Thread.sleep(5000);
              //  List<Employee> emp = employeeServiceImpl.readEmployees().join();
                employeeServiceImpl.readEmpFirstNames().subscribe((str)->{
                	names.add(str);
                });
               // System.out.println("size: " + emp.size());
                return names.get(0);
            }
        };
		return task;
	}
	
	@RequestMapping(value="/web/webasync.html")
	public WebAsyncTask<String> longTimeTask(){
	   
	    Callable<String> callable = new Callable<String>() {
	        public String call() throws Exception {
	            Thread.sleep(3000); 
	            logger.info("controller#longTimeTask task started.");
	            System.out.println("controller#webasync task started. Thread: " +
                        Thread.currentThread()
                              .getName());
	            return "Tral";
	        }
	    };
	    return new WebAsyncTask<String>(callable);
	}
	
	

}
