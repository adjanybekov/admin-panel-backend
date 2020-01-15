package com.example.test1.controller;

import com.example.test1.dto.Coordinates;
import com.example.test1.dto.ParkingDto;
import com.example.test1.model.Parking;
import com.example.test1.repo.ParkingRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@CrossOrigin(methods = {RequestMethod.GET,RequestMethod.DELETE,RequestMethod.PUT,RequestMethod.POST},origins = "*",maxAge = 3600)
public class AdminController {
    
    @Autowired
    ParkingRepo parkingRepo;
    @Autowired
    private RestTemplate restTemplate2;

//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIkFkbWluIFVzZXIiXSwiaWF0IjoxNTc5MDgyNjY3LCJleHAiOjE1NzkwODYyNjd9.wPZKjKJgrcN34dOKT_W3bhdyxW8adKTlr09o3BiQV0M";

    String url = "http://localhost:5000/api/v1/parking-location";

    @GetMapping("/parkings")
    public ResponseEntity<List<ParkingDto>> getParkings(HttpServletRequest request, HttpServletResponse response){
//        List<Parking> all = parkingRepo.findAll();


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",String.format("Bearer_%s",token));
        String getLst = String.format("%s/list",url);

        HttpEntity<String> entity = new HttpEntity<>("body",headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ParkingDto[]> all = restTemplate.exchange(getLst, HttpMethod.GET,entity,ParkingDto[].class);

        List<ParkingDto> parkingDtos = Arrays.asList(all.getBody());

//        response.setHeader("X-Total-Count",""+parkingDtos.size());
        response.setHeader("Content-Range","posts 0-10/"+parkingDtos.size());
//        response.setHeader("Access-Control-Expose-Headers","X-Total-Count");
        response.setHeader("Access-Control-Expose-Headers","Content-Range");
        return new ResponseEntity<>(parkingDtos, HttpStatus.OK);
    }

    @GetMapping("/parkings/{id}")
    public ResponseEntity editParking(@PathVariable("id") String id){
//        Parking parking = new Parking();



        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",String.format("Bearer_%s",token));
        String getLst = String.format("%s/%s",url,id);

        HttpEntity<String> entity = new HttpEntity<>("body",headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ParkingDto> saveResponse = restTemplate.exchange(getLst, HttpMethod.GET,entity,ParkingDto.class);
        ParkingDto save = saveResponse.getBody();


        return new ResponseEntity(save,HttpStatus.OK);
    }

    @PostMapping("/parkings")
    public ResponseEntity createParking(@RequestBody ParkingDto parkingDto){

//        parking.setLatitude(parkingDto.getCoordinates().getLat().toString());
//        parking.setLongitude(parkingDto.getCoordinates().getLng().toString());
        String createUrl = String.format("%s/create",url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",String.format("Bearer_%s",token));
        HttpEntity entity = new HttpEntity(parkingDto,headers);

        Map map = restTemplate2.postForObject(createUrl,entity,Map.class);
        map.put("id","11");
        return new ResponseEntity(map,HttpStatus.OK);
    }


    @PutMapping("/parkings/{id}")
    public ResponseEntity editParking(@PathVariable("id") String id, @RequestBody ParkingDto parkingDto){

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",String.format("Bearer_%s",token));

        HttpEntity entity = new HttpEntity(parkingDto,headers);
        String editParking = String.format("%s/edit/%s",url,id);
        Map responseEntity = restTemplate2.postForObject(editParking,entity,Map.class);
        return new ResponseEntity(responseEntity,HttpStatus.OK);
    }

    @CrossOrigin
    @DeleteMapping("/parkings/{id}")
    public ResponseEntity deleteParking(@PathVariable("id") String id){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",String.format("Bearer_%s",token));

        HttpEntity entity = new HttpEntity(headers);
        String urlDelete = String.format("%s?id=",url);


        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url+"/")
                .queryParam("id",id);
        ResponseEntity re = restTemplate2.exchange(builder.toUriString(),
                HttpMethod.DELETE,entity,
                ResponseEntity.class);

        return new ResponseEntity(id,HttpStatus.OK);
    }
}
