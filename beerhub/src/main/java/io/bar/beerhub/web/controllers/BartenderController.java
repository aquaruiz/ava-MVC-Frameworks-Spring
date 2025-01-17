package io.bar.beerhub.web.controllers;

import io.bar.beerhub.errors.BeerNotFoundException;
import io.bar.beerhub.services.factories.BeerService;
import io.bar.beerhub.services.factories.CashService;
import io.bar.beerhub.services.factories.OrderService;
import io.bar.beerhub.services.models.BeerServiceModel;
import io.bar.beerhub.services.models.OrderServiceModel;
import io.bar.beerhub.web.models.BeerBuyModel;
import io.bar.beerhub.web.models.BeerListingModel;
import io.bar.beerhub.web.models.BeerRunoutModel;
import io.bar.beerhub.web.models.OrderViewModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bartender")
@RolesAllowed({"ROLE_ADMIN", "ROLE_ROOT", "ROLE_BARTENDER"})
//@PreAuthorize("hasRole('ROLE_BARTENDER')")
public class BartenderController extends BaseController {
    private final BeerService beerService;
    private final OrderService orderService;
    private final CashService cashService;
    private final ModelMapper modelMapper;

    @Autowired
    public BartenderController(BeerService beerService, OrderService orderService, CashService cashService, ModelMapper modelMapper) {
        this.beerService = beerService;
        this.orderService = orderService;
        this.cashService = cashService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/storage")
    public ModelAndView getStorage(ModelAndView modelAndView) {
        List<BeerServiceModel> beers = this.beerService.getAllBeers();
        List<BeerListingModel> beerListing = beers
                .stream()
                .map(b -> this.modelMapper.map(b, BeerListingModel.class))
                .collect(Collectors.toList());
        modelAndView.addObject("beers", beerListing);
        return view("bartender/storage", modelAndView);
    }

    @GetMapping("/cash")
    public ModelAndView getCash(ModelAndView modelAndView) {
        BigDecimal cash = this.cashService.getCurrentCash();
        modelAndView.addObject("cash", cash);
        return view("bartender/cash", modelAndView);
    }

    @GetMapping("/runouts")
    public ModelAndView getRunouts(ModelAndView modelAndView) {
        List<BeerServiceModel> beers = this.beerService.findAllRunoutsBeers(40L);
        List<BeerRunoutModel> runoutBeers = beers
                .stream()
                .map(b -> this.modelMapper.map(b, BeerRunoutModel.class))
                .collect(Collectors.toUnmodifiableList());
        modelAndView.addObject("beers", runoutBeers);
        modelAndView.addObject("title", "BEER HOME");
        return view("bartender/runouts", modelAndView);
    }

    @PostMapping("/buy")
    public ModelAndView buyBeer(@ModelAttribute BeerBuyModel beerBuyModel) {
        BeerServiceModel beerToBuy = this.modelMapper.map(beerBuyModel, BeerServiceModel.class);
        Long quantity = beerBuyModel.getQuantity();
        this.beerService.buyBeer(beerToBuy, quantity);
        return redirect("runouts");
    }

    @ExceptionHandler(BeerNotFoundException.class)
    public ModelAndView handleException(BeerNotFoundException exception) {
        ModelAndView modelAndView = new ModelAndView("custom-error");
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }

    @GetMapping("/all-orders")
    public ModelAndView listAllOrders(ModelAndView modelAndView) {
        List<OrderServiceModel> orders = this.orderService.listAllOrders();
        List<OrderViewModel> orderListing = orders
                .stream()
                .map(b -> this.modelMapper.map(b, OrderViewModel.class))
                .collect(Collectors.toList());
        modelAndView.addObject("orders", orderListing);
        return view("bartender/all-orders", modelAndView);
    }

}