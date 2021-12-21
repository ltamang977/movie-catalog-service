package org.ltamang.moviecatalogservice;

import java.util.ArrayList;
import java.util.List;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.ltamang.moviecatalogservice.model.Movie;
import org.ltamang.moviecatalogservice.model.MovieCatalogItem;
import org.ltamang.moviecatalogservice.model.Ratings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    @Autowired
    @LoadBalanced
	RestTemplate restTemplate;
    
    @RequestMapping("/{movieId}")
    @HystrixCommand(fallbackMethod = "getFallbackCatalog")
    public MovieCatalogItem getCatalog(@PathVariable("movieId") String  movieId){

        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+movieId, Movie.class);

        Ratings ratings = restTemplate.getForObject("http://ratings-data-service/ratings/"+movieId, Ratings.class);

        MovieCatalogItem movieCatalog = new MovieCatalogItem(movie.getName(), movie.getName(), ratings.getRating());
        return movieCatalog;

    }

    public MovieCatalogItem getFallbackCatalog(@PathVariable("movieId") String  movieId){

        MovieCatalogItem movieCatalog = new MovieCatalogItem("Fallback Movie", "Fallback Movie", 5);
        return movieCatalog;

    }
}
