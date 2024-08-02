package com.sjprogramming.restapi.service;

import java.util.List;


import com.sjprogramming.restapi.entity.Students;

public interface ProductService {
	
	public Students saveProduct(Students product);
	
	public List<Students> getAllProduct();
	
	public Students getProductById(Integer id);
	
	public String deleteProduct(Integer id);
	public Students editProduct(Students product,Integer id);

}
