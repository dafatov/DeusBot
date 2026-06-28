package ru.demetrious.deus.bot.domain.game;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Accessors(chain = true)
public class Pack {
    @GeneratedValue(strategy = IDENTITY)
    @Id
    private Long id;
    private String name;
    @ManyToMany(cascade = {PERSIST, MERGE, REFRESH, DETACH}, fetch = EAGER)
    @JoinTable(name = "pack_word",
        joinColumns = @JoinColumn(name = "pack_id"),
        inverseJoinColumns = @JoinColumn(name = "word"))
    private Set<Word> words;
}
