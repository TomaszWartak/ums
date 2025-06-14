# Zadanie rekrutacyjne: System zarządzania użytkownikami (DDD, REST API, Java)

## Cel

Celem zadania jest stworzenie prostego systemu do zarządzania użytkownikami (User Management System), który będzie zgodny z zasadami czystej architektury (Clean Architecture) i podejścia Domain-Driven Design (DDD). Projekt powinien zostać zaimplementowany w języku Java, z wykorzystaniem frameworków i bibliotek wspierających dobre praktyki.

---

## Opis funkcjonalny

System powinien umożliwiać:

1. Dodanie nowego użytkownika  
   - Dane użytkownika (imię, nazwisko, e-mail itp.) mają być zapisane w relacyjnej bazie danych (dowolna).  
   - Po zapisie API powinno zwrócić identyfikator użytkownika.  
2. Pobranie listy wszystkich użytkowników  
3. Aktywację użytkownika  
   - Zmiana statusu użytkownika na aktywny.  
4. Dezaktywację użytkownika  
   - Zmiana statusu użytkownika na nieaktywny.

---

## Stos technologiczny

- Java 21  
- Spring Boot 3+  
- Maven  
- Testy jednostkowe
