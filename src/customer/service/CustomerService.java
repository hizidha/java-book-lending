package customer.service;

import DB.DB;
import common.GlobalService;

import customer.model.Customer;
import customer.repository.CustomerRepository;
import customer.service.exception.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CustomerService implements GlobalService<Customer> {
    CustomerRepository customerRepository;

    public CustomerService() throws SQLException {
        Connection connection = DB.connect();
        this.customerRepository = new CustomerRepository(connection);
    }

    @Override
    public List<Customer> getAll() {
        return this.customerRepository.getAll();
    }

    @Override
    public Customer getBy(int id) {
        return this.customerRepository.findBy(id)
                .orElseThrow(CustomerNotFoundException::new);
    }

    public Customer getBy(String name) {
        return this.customerRepository.findBy(name)
                .orElseThrow(CustomerNotFoundException::new);
    }

    public List<Customer> getCustomerListBy(String name) {
        return this.customerRepository.findCustomerListBy(name);
    }

    @Override
    public int deleteBy(int id) {
        return this.customerRepository.delete(id);
    }

    public int create(Customer customer) {
        int existingCustomer = this.customerRepository.checkCustomer(customer.getName());
        if (existingCustomer > 0) {
            throw new CustomerHasBeenAddedException();
        }

        int newCustomer = this.customerRepository.add(customer);
        if (newCustomer <= 0) {
            throw new FailedToAddCustomerException();
        }
        return newCustomer;
    }

    public int update(Customer customer, String name) {
        Customer existingCustomer = this.getBy(customer.getId());

        if (existingCustomer == null) {
            throw new CustomerNotFoundException();
        }

        Customer updatedCustomer = new Customer(existingCustomer.getId(), name);
        int result = this.customerRepository.update(updatedCustomer);

        if (result <= 0) {
            throw new FailedToUpdateCustomerException();
        }
        return result;
    }
}