package org.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.model.ReservationModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.testng.Assert.assertTrue;


public class ConcurrentBookingTest {


    RestTemplate restTemplate = new RestTemplate();

    String requestBody = "{\n" +
            "    \"campSiteId\": 3,\n" +
            "    \"startDate\": \"2021-01-09\",\n" +
            "    \"endDate\": \"2021-01-09\",\n" +
            "    \"email\": \"abc@gmail.com\",\n" +
            "    \"name\": \"custPostman\"\n" +
            "}";

    @Test
    public void testConcurrency() throws ExecutionException, InterruptedException, JsonProcessingException {
        deleteAllReservations();

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        List<Future<Long>> futures = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            futures.add(executorService.submit(new PostBooking()));
        }
        for (Future<Long> future :futures){
            Long aLong = future.get();
            System.out.println("result: "+aLong);
        }
        String response = getAllReservations();
        ObjectMapper mapper = new ObjectMapper();
        ReservationModel[] reservationModels = mapper.readValue(response, ReservationModel[].class);
        assertTrue(reservationModels.length ==1);
    }

    private String getAllReservations(){
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/reservation", String.class);
        return response.getBody();
    }
    private void deleteAllReservations(){
        restTemplate.delete("http://localhost:8080/reservationQA");
    }

    private class PostBooking implements Callable<Long> {

        String requestBody = "{\n" +
                "    \"campSiteId\": 3,\n" +
                "    \"startDate\": \"2021-01-09\",\n" +
                "    \"endDate\": \"2021-01-09\",\n" +
                "    \"email\": \"abc@gmail.com\",\n" +
                "    \"name\": \"custPostman\"\n" +
                "}";

        @Override
        public Long call() throws Exception {
            Long result = -1l;
            try{
                String url = "http://localhost:8080/reservation";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);

                result = Long.valueOf(restTemplate.postForObject(url, request, String.class));
                System.out.println("finished: "+result);
            } catch (Exception e){
                System.out.println("caught exception: "+e.getMessage());
            }
            return Long.valueOf(result);
        }
    }
}
