package io.bar.beerhub.web.controllers;

import io.bar.beerhub.services.factories.BeerService;
import io.bar.beerhub.services.models.BeerServiceModel;
import io.bar.beerhub.util.factory.EscapeCharsUtil;
import io.bar.beerhub.web.annotations.PageTitle;
import io.bar.beerhub.web.models.BeerCreateModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/beers")
public class BeerController extends BaseController {
    private final BeerService beerService;
    private final EscapeCharsUtil escapeCharsUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public BeerController(BeerService beerService, EscapeCharsUtil escapeCharsUtil, ModelMapper modelMapper) {
        this.beerService = beerService;
        this.escapeCharsUtil = escapeCharsUtil;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/create")
    public ModelAndView getCreateBeer(ModelAndView modelAndView) {
        modelAndView.addObject("title", "BEER HOME");
        return view("beers/create", modelAndView);
    }

    @PostMapping("/create")
    public ModelAndView createBeer(@ModelAttribute BeerCreateModel beerCreateModel, HttpSession session) {
        beerCreateModel = escapeCharsUtil.escapeChars(beerCreateModel);

        BeerServiceModel beerServiceModel = this.modelMapper.map(beerCreateModel, BeerServiceModel.class);
        this.beerService.save(beerServiceModel);
        return redirect("/bartender/storage");
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_ROOT", "ROLE_BARTENDER", "ROLE_CUSTOMER"})
    @PageTitle("Beer Delete")
    @GetMapping("/details/{id}")
    public ModelAndView delete(@PathVariable String id, ModelAndView modelAndView){
        BeerServiceModel beerServiceModel = this.beerService.findOneById(id);

        modelAndView.addObject("beer", beerServiceModel);
        return this.view("/beers/details", modelAndView);
    }


    @PostMapping("/edit")
    public ModelAndView editBeer(@ModelAttribute BeerServiceModel beerServiceModel, HttpSession session) {
        beerServiceModel = escapeCharsUtil.escapeChars(beerServiceModel);

        this.beerService.edit(beerServiceModel);
        return redirect("/bartender/storage");
    }
}