package com.example.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Beer;
import com.example.demo.domain.BeerRepository;

// first RESTful web service

@RestController
public class BeerController {
	
	@Autowired
	private BeerRepository beerRepository;
	
	@RequestMapping("/beers")
	public Iterable<Beer> getBeers(){
		return this.beerRepository.findAll();
	}
}
