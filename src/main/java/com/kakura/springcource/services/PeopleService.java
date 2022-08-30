package com.kakura.springcource.services;

import com.kakura.springcource.models.Book;
import com.kakura.springcource.models.Person;
import com.kakura.springcource.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private static final long MILLISECONDS_TO_DAYS = 1000 * 60 * 60 * 24;
    private final PeopleRepository peopleRepository;

    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        return peopleRepository.findById(id).orElse(null);
    }

    public List<Book> getBooksByPersonId(int id) {
        Optional<Person> person = peopleRepository.findById(id);
        if (person.isPresent()) {
            Hibernate.initialize(person.get().getBooks());
            person.get().getBooks().forEach(book -> {
                long diffInMilliseconds = new Date().getTime() - book.getTakenAt().getTime();
                long daysAgoTaken = diffInMilliseconds / MILLISECONDS_TO_DAYS;

                if (daysAgoTaken > 10) {
                    book.setExpired(true);
                }
            });

            return person.get().getBooks();
        } else {
            return Collections.emptyList();
        }
    }

    public Optional<Person> getPersonByName(String name) {
        return peopleRepository.findByName(name);
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }
}
