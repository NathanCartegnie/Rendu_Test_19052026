package org.example;

import java.util.List;
import java.util.stream.Collectors;

public class RechercheVille {

    private List<String> villes;

    public RechercheVille(List<String> villes) {
        this.villes = villes;
    }

    public List<String> Rechercher(String mot) throws NotFoundException {
        if (mot.equals("*")) {
            return villes;
        }

        if (mot.length() < 2) {
            throw new NotFoundException("Le texte de recherche doit contenir au moins 2 caractères");
        }

        return villes.stream()
                .filter(ville -> ville.toLowerCase().contains(mot.toLowerCase()))
                .collect(Collectors.toList());
    }
}