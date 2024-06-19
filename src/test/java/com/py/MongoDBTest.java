package com.py;

import com.py.domain.Game;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MongoDBTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Test
    public void test1(){
        Game game = new Game();
        ArrayList<ArrayList<Integer>> arrayList = new ArrayList<>();
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        integerArrayList.add(1);
        integerArrayList.add(2);
        arrayList.add(integerArrayList);
        ArrayList<Integer> integerArrayList1 = new ArrayList<>(integerArrayList);
        arrayList.add(integerArrayList1);
        game.setId(0);
        game.setTypes(arrayList);
        game.setAnswer(new ArrayList<>(1));
        game.setDifficulty(1);
        game.setValues(new ArrayList<>(1));
        Game game1 = mongoTemplate.insert(game);
        System.out.println(game1);
    }
    @Test
    public void getCount(){
        Query query = new Query();
        long collectionName = mongoTemplate.count(query, Game.class);
        System.out.println(collectionName);
    }
    @Test
    public void mongodb(){
        Query query = new Query(Criteria.where("Id").is(0));
        Game games = mongoTemplate.findOne(query, Game.class);
        System.out.println(games);
    }
}
