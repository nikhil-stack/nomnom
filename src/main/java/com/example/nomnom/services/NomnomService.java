package com.example.nomnom.services;

import com.example.nomnom.models.Nomnom;
import com.example.nomnom.repositories.NomnomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class NomnomService {

    private final NomnomRepository nomnomRepository;

    @Autowired
    public NomnomService(NomnomRepository nomnomRepository){
        this.nomnomRepository = nomnomRepository;
    }

    public List<Nomnom> getNomnom(){
        try {
            return nomnomRepository.findAll();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return List.of();
        }
    }

    public String createNomNom(Nomnom nomnom) {
        try{
            nomnomRepository.save(nomnom);
            return "";

        } catch (Exception e){
            System.out.println(e.getMessage());
            return "Error Occurred!";
        }
    }

    public Optional<Nomnom> getASingleNomnom(Long id){
        try {

            return nomnomRepository.findById(id);

        } catch (Exception e){
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Nomnom> editNomnom(Long id, Nomnom nomnom, String username){
        try {
            Optional<Nomnom> optionalNomnomById =  nomnomRepository.findById(id);
            if(optionalNomnomById.isEmpty()){
                return Optional.empty();
            }

            Nomnom nomnomById = optionalNomnomById.get();
            String userEmail = nomnomById.getUserEmail();

            if(!username.equals(userEmail) ){
                return Optional.empty();
            }



            String icon = nomnom.getIcon();
            String description = nomnom.getDescription();

            if(icon != null){
                nomnomById.setIcon(icon);
            }
            if(description != null){
                nomnomById.setDescription(description);
            }

            nomnomById.setUpdatedAt(LocalDateTime.now());

            Nomnom updatedNomnom = nomnomRepository.save(nomnomById);

            return Optional.of(updatedNomnom);


        } catch (Exception e){
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public String deleteNomnom(Long id, String username){
        try {
            Optional<Nomnom> optionalNomnomById =  nomnomRepository.findById(id);
            if(optionalNomnomById.isEmpty()){
                return "No nomnom found";
            }

            Nomnom nomnomById = optionalNomnomById.get();
            String userEmail = nomnomById.getUserEmail();

            if(!username.equals(userEmail) ){
                return "User not authenticated";
            }

            nomnomRepository.deleteById(id);


            return "Nomnom deleted successfully";


        } catch (Exception e){
            System.out.println(e.getMessage());
            return "Bad request";
        }
    }
}
