[![Codacy Badge](https://api.codacy.com/project/badge/Grade/95c3420ca4c242cb86f17a70580d6e4b)](https://www.codacy.com/app/petrovale/topjava10?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=petrovale/topjava10&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/petrovale/topjava10.svg?branch=master)](https://travis-ci.org/petrovale/topjava10)
[![Dependency Status](https://dependencyci.com/github/petrovale/topjava10/badge)](https://dependencyci.com/github/petrovale/topjava10)

Java Enterprise Online Project 
===============================
Java Enterprise проект с регистрацией/авторизацией и интерфейсом на основе ролей (USER, ADMIN). Администратор может создавать/редактировать/удалять/пользователей, а пользователь - управлять своим профилем и данными (день, еда, калории) через UI (по AJAX) и по REST интерфейсу с базовой авторизацией. Возможна фильтрация данных по датам и времени, при этом цвет записи таблицы еды зависит от того, превышает ли сумма калорий за день норму (редактируемый параметр в профиле пользователя). Весь REST интерфейс покрывается JUnit тестами, используя Spring MVC Test и Spring Security Test.
[Демо приложения](https://topjava10alexey.herokuapp.com/)