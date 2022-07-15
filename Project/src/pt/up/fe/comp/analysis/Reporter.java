package pt.up.fe.comp.analysis;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import java.util.List;

public interface Reporter {

    List<Report> getReports();

    void addReport(JmmNode node, String message);
}
