package com.livestock.specifications.authorization;

import com.livestock.models.dtos.authorization.user.UserFilterDto;
import com.livestock.models.entities.authorization.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserSpecification {

    public static Specification<User> withFilters(UserFilterDto f) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (f.getFullName() != null && !f.getFullName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("fullName")),
                        "%" + f.getFullName().toLowerCase() + "%"));
            }
            if (f.getEmail() != null && !f.getEmail().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")),
                        "%" + f.getEmail().toLowerCase() + "%"));
            }
            if (f.getRole() != null && !f.getRole().isBlank()) {
                predicates.add(cb.equal(root.get("role").get("name"), f.getRole()));
            }
            if (f.getDisabled() != null) {
                predicates.add(cb.equal(root.get("disabled"), f.getDisabled()));
            }
            if (f.getCreatedAtFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), f.getCreatedAtFrom()));
            }
            if (f.getCreatedAtTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), f.getCreatedAtTo()));
            }
            if (f.getSearch() != null && !f.getSearch().isBlank()) {
                String like = "%" + f.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullName")), like),
                        cb.like(cb.lower(root.get("email")), like)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Exclut l'utilisateur ayant l'id donné du résultat.
     * Utilisé notamment pour retirer l'utilisateur courant d'un listing admin.
     */
    public static Specification<User> excludingId(UUID id) {
        if (id == null) {
            return null;
        }
        return (root, query, cb) -> cb.notEqual(root.get("id"), id);
    }
}