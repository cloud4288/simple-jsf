package com.example.simplejsf.view;

import com.example.simplejsf.converter.UserConverter;
import com.example.simplejsf.model.BankAccount;
import com.example.simplejsf.model.User;
import com.example.simplejsf.service.UserService;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author pgutierrez
 */
@ViewScoped
@Component
public class AccountView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;
    
    private Map<String, String> usersToTransfer;
    
    private List<User> users;
    
    private User selectedUser;

    private User userToTransfer;
    
    private double money;

    @PostConstruct
    public void init() {
        users = userService.findAllUsers();
        
        usersToTransfer = new HashMap<>();
        
        users.forEach(u -> {
           usersToTransfer.put(String.format("%s %s %s", u.getFirstName(), u.getMiddleName(), u.getLastName()), u.getId() + "");
        });
    }

    public void transfer() {
        BankAccount selectedBankAccount = selectedUser.getBankAccounts().get(0);
        double selectedExistingBalance = selectedBankAccount.getBalance();
        
        BankAccount userToTransferBankAccount = userToTransfer.getBankAccounts().get(0);
        double userToTransferExistingBalance = userToTransferBankAccount.getBalance();
        
        // TODO add transaction management here later
        
        selectedBankAccount.setBalance(selectedExistingBalance - money);
        userService.update(selectedUser);
        
        userToTransferBankAccount.setBalance(userToTransferExistingBalance + money);
        userService.update(userToTransfer);
        
        // TODO find better solution to this
        users = userService.findAllUsers();
        
        FacesContext.getCurrentInstance().addMessage("Success", new FacesMessage("Success", "Transfer was successful"));
        
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('transferDialog').hide()");
    }
    
    public void selectBalance(User user) {
        selectedUser = user;
    }

    public void withdraw() {
        BankAccount bankAccount = selectedUser.getBankAccounts().get(0);
        double existingBalance = bankAccount.getBalance();
        bankAccount.setBalance(existingBalance - money);

        userService.update(selectedUser);

        FacesContext.getCurrentInstance().addMessage("Success", new FacesMessage("Success",
                "Withdraw of " + money + " was successful"));

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('withdrawDialog').hide()");
        requestContext.update("accountTable");
    }

    public void deposit() {
        BankAccount bankAccount = selectedUser.getBankAccounts().get(0);
        double existingBalance = bankAccount.getBalance();
        bankAccount.setBalance(existingBalance + money);

        userService.update(selectedUser);

        FacesContext.getCurrentInstance().addMessage("Success", new FacesMessage("Success",
                "deposit of " + money + " was successful"));

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('depositDialog').hide()");
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public Map<String, String> getUsersToTransfer() {
        return usersToTransfer;
    }

    public void setUsersToTransfer(Map<String, String> usersToTransfer) {
        this.usersToTransfer = usersToTransfer;
    }

    public User getUserToTransfer() {
        return userToTransfer;
    }

    public void setUserToTransfer(User userToTransfer) {
        this.userToTransfer = userToTransfer;
    }

    public UserConverter getUserConverter() {
        return userConverter;
    }

    public void setUserConverter(UserConverter userConverter) {
        this.userConverter = userConverter;
    }
}
