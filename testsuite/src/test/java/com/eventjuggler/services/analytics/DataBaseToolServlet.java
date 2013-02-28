package com.eventjuggler.services.analytics;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/clear")
public class DataBaseToolServlet extends HttpServlet {

    @Inject
    private DataBaseTool dataBaseTool;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dataBaseTool.clearEvents();
    }

}
