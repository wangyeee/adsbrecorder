package adsbrecorder.receiver.repo.impl;

import static java.util.Objects.requireNonNull;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.repo.TrackingRecordDataRepository;

@Repository
public class TrackingRecordDataRepositoryImpl implements TrackingRecordDataRepository {

    private MongoTemplate mongoTemplate;

    @Autowired
    public TrackingRecordDataRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = requireNonNull(mongoTemplate);
    }

    public Map<String, Date> recentFlights(int amount) {
        Map<String, Date> flights = new HashMap<String, Date>();
        Aggregation aggregation = newAggregation(
                group("flight").max("recordDate").as("recordDate"),
                sort(Sort.Direction.DESC, "recordDate"),
                limit(amount));
        @SuppressWarnings("rawtypes")
        AggregationResults<Map> r = mongoTemplate.aggregate(aggregation, TrackingRecord.class, Map.class);
        r.getMappedResults().forEach(q -> flights.put(String.valueOf(q.get("_id")), (Date) q.get("recordDate")));
        return flights;
    }
}
