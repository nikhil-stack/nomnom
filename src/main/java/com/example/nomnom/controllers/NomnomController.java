package com.example.nomnom.controllers;

import com.example.nomnom.models.Nomnom;
import com.example.nomnom.services.NomnomService;
import com.example.nomnom.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "nomnom")
@CrossOrigin(origins = "http://localhost:5173",  allowCredentials = "true")
public class NomnomController {
    private final NomnomService nomnomService;

    @Autowired
    public NomnomController(NomnomService nomnomService){
        this.nomnomService = nomnomService;
    }

    @GetMapping("/")
    public ApiResponse<List<Nomnom>> getNomnom() {
        List<Nomnom> nomnoms = nomnomService.getNomnom();

        if(nomnoms.isEmpty()){
            return ApiResponse.failureResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "No nomnoms found!");
        }
        else{
            return ApiResponse.successResponse(nomnoms);
        }
    }

    @PostMapping
    public ApiResponse<Nomnom> createNomnom(@RequestBody Nomnom nomnom) {
        try {
            final String name = nomnom.getName();
            final String icon = nomnom.getIcon();

            if(name.isEmpty() || icon.isEmpty()){
                return ApiResponse.failureResponse(HttpStatus.BAD_REQUEST.value(), "Name/Icon can't be null");
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            nomnom.setUserEmail(username);
            LocalDateTime currentTime = LocalDateTime.now();
            nomnom.setCreatedAt(currentTime);
            nomnom.setUpdatedAt(currentTime);

            String response = nomnomService.createNomNom(nomnom);

            if(!response.isEmpty()){
                return ApiResponse.failureResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), response);
            }
            else{
                return ApiResponse.successResponse(nomnom);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ApiResponse.failureResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Bad Request");
        }
    }

    @GetMapping("/getnomnom")
    public ApiResponse<Nomnom> getASingleNomnom(@RequestParam(name = "id", defaultValue = "") Long id){

        Optional<Nomnom> nomnom =  nomnomService.getASingleNomnom(id);

        if(nomnom.isEmpty()){
            return ApiResponse.failureResponse(HttpStatus.NO_CONTENT.value(), "No nomnoms found");
        }

        else {
            return ApiResponse.successResponse(nomnom.get());
        }


    }

    @PatchMapping("/edit")
    public ApiResponse<Nomnom> editNomnom(@RequestParam(name = "id", defaultValue = "") Long id, @RequestBody Nomnom nomnom){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<Nomnom> editedNomnom = nomnomService.editNomnom(id, nomnom, username);

        if(editedNomnom.isEmpty()){
            return ApiResponse.failureResponse(HttpStatus.BAD_REQUEST.value(), "Can't edit nomnom");
        }
        else{
            return ApiResponse.successResponse(editedNomnom.get());
        }
    }

    @DeleteMapping("/delete")
    public ApiResponse<String> deleteNomnom(@RequestParam(name = "id", defaultValue = "") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        String response = nomnomService.deleteNomnom(id, username);

        if(response.equals("Nomnom deleted successfully")){
            return ApiResponse.successResponse(response);
        }
        else {
            return ApiResponse.failureResponse(HttpStatus.BAD_REQUEST.value(), response);
        }

    }
}
