package com.ashe.anon;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.*;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.criteria.*;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.DataSource;
import org.deidentifier.arx.metric.Metric;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import org.json.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Path("/anon")
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to Ashe. Data Anonymity Made Simple!\n");

        final String BASE_URI = "http://localhost:8000/";
        final ResourceConfig rc = new ResourceConfig().packages("com.ashe.anon").register(MultiPartFeature.class);
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        System.out.println("Server: " + BASE_URI + " (WADL: " + BASE_URI + "application.wadl)");

        System.in.read();
        server.shutdownNow();
    }

    private String username = null;
    private String password = null;

    private static String path = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(String creds) throws IOException {
        path = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        File dir = new File(path + "/ashe");

        if (!dir.exists()) {
            new File(path + "/ashe").mkdir();
            new File(path + "/ashe/DataSources").mkdir();
            new File(path + "/ashe/Configurations").mkdir();
            new File(path + "/ashe/Hierarchies").mkdir();
            new File(path + "/ashe/Results").mkdir();
        }

        System.out.println(creds);

        try {
            JSONObject json = new JSONObject(creds);
            username = json.getString("username");
            password = json.getString("password");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (username.equals("admin") && password.equals("admin")) {
            return Response.status(200)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers",
                            "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods",
                            "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .entity("User Authenticated!").build();
        } else {
            return Response.status(401)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers",
                            "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods",
                            "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .entity("User Not Authenticated!").build();
        }
    }

    @POST
    @Path("/uploadSource")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {
        path = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        String UPLOAD_PATH = path + "/ashe/DataSources/" + fileDetail.getFileName();

        try {
            System.out.println("File: " + fileDetail.getFileName());
            int read = 0;
            byte[] bytes = new byte[1024];

            OutputStream out = new FileOutputStream(new File(UPLOAD_PATH));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            out.flush();
            out.close();
        } catch (IOException e) {
            throw new WebApplicationException("Error while uploading file. Please try again.");
        }

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .entity("Data Source Uploaded!").build();
    }

    @POST
    @Path("/uploadConfig")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadConfig(String text) {
        path = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        String UPLOAD_PATH = path + "/ashe/Configurations/";

        try {
            JSONObject config = new JSONObject(text);

            JSONArray attributes = config.getJSONArray("attributes");
            JSONObject privacy_config = config.getJSONObject("privacy_config");

            java.nio.file.Path path = Paths.get(UPLOAD_PATH + "config.xml");

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
                writer.write("<config xmlns=\"http://www.iiit.ac.in/config\"\n");
                writer.write("        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
                writer.write("        xsi:schemaLocation=\"http://www.iiit.ac.in/config config.xsd\">\n\n");

                writer.write("    <AttributeData>\n");
                for (int i = 0; i < attributes.length(); i++) {
                    JSONObject attribute = attributes.getJSONObject(i);
                    writer.write(
                            "        <Attribute attribute_name=\"" + attribute.getString("attribute_name") + "\">\n");
                    writer.write("            <AttributeType>" + attribute.getString("attributeType")
                            + "</AttributeType>\n");
                    writer.write("            <DataType>" + attribute.getString("DataType") + "</DataType>\n");
                    writer.write("        </Attribute>\n");
                }
                writer.write("    </AttributeData>\n\n");

                writer.write("    <PrivacyModel>\n");
                writer.write("        <Model k=\"" + privacy_config.getString("k") + "\">"
                        + privacy_config.getString("model") + "</Model>\n");
                writer.write("        <SuppressionRate>" + privacy_config.getString("rate") + "</SuppressionRate>\n");
                writer.write("    </PrivacyModel>\n\n");

                writer.write("</config>\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .entity("Configuration Uploaded!").build();
    }

    @POST
    @Path("/uploadHierarchy")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadHierarchy(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {
        path = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        String UPLOAD_PATH = path + "/ashe/Hierarchies/" + fileDetail.getFileName();

        try {
            System.out.println("File: " + fileDetail.getFileName());
            int read = 0;
            byte[] bytes = new byte[1024];

            OutputStream out = new FileOutputStream(new File(UPLOAD_PATH));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            out.flush();
            out.close();
        } catch (IOException e) {
            throw new WebApplicationException("Error while uploading file. Please try again.");
        }

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .entity("Hierarchy Uploaded!").build();
    }

    @POST
    @Path("/anonymize")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response anonymize(String req) throws IOException {
        try {
            JSONObject data = new JSONObject(req);

            String dataSource = data.getString("data");

            String dataSourcePath = path + "/ashe/DataSources/" + dataSource;
            String configSourcePath = path + "/ashe/Configurations/config.xml";
            String hierarchyPath = path + "/ashe/Hierarchies/" + dataSource.substring(0, dataSource.indexOf("."))
                    + "_hierarchy_";

            File dataSourceFile = new File(dataSourcePath);
            File configSourceFile = new File(configSourcePath);

            DataSource dataSourceObj = DataSource.createCSVSource(dataSourceFile, Charset.defaultCharset(), ',', true);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(configSourceFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("AttributeData");
            Node nNode = nList.item(0);
            Element eElement = (Element) nNode;

            NodeList attributes = eElement.getElementsByTagName("Attribute");

            for (int i = 0; i < attributes.getLength(); ++i) {
                Node node = attributes.item(i);
                dataSourceObj.addColumn(node.getAttributes().item(0).getNodeValue());
            }

            Data dataObj = Data.create(dataSourceObj);
            DataDefinition dataDef = dataObj.getDefinition();

            for (int i = 0; i < attributes.getLength(); ++i) {
                Node node = attributes.item(i);
                String attribute = node.getAttributes().item(0).getNodeValue();

                Element element = (Element) node;
                NodeList attributeTypes = element.getElementsByTagName("AttributeType");
                String attributeType = attributeTypes.item(0).getTextContent();

                if (attributeType.equals("QUASI_IDENTIFYING")) {
                    File hierarchyFile = new File(hierarchyPath + attribute + ".csv");
                    Hierarchy hierarchy = null;

                    if (hierarchyFile.exists())
                        hierarchy = Hierarchy.create(hierarchyPath + attribute + ".csv", Charset.defaultCharset(), ',');

                    dataDef.setAttributeType(attribute, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);

                    if (hierarchyFile.exists())
                        dataDef.setAttributeType(attribute, hierarchy);
                } else if (attributeType.equals("IDENTIFYING")) {
                    dataDef.setAttributeType(attribute, AttributeType.IDENTIFYING_ATTRIBUTE);
                } else if (attributeType.equals("SENSITIVE")) {
                    dataDef.setAttributeType(attribute, AttributeType.SENSITIVE_ATTRIBUTE);
                } else {
                    dataDef.setAttributeType(attribute, AttributeType.INSENSITIVE_ATTRIBUTE);
                }
            }

            NodeList privacyModel = doc.getElementsByTagName("PrivacyModel");
            Node privacyModelNode = privacyModel.item(0);
            Element privacyModelElement = (Element) privacyModelNode;

            NodeList model = privacyModelElement.getElementsByTagName("Model");
            NodeList suppressionRate = privacyModelElement.getElementsByTagName("SuppressionRate");

            String modelType = model.item(0).getTextContent();
            String suppressionRateValue = suppressionRate.item(0).getTextContent();

            ARXConfiguration config = ARXConfiguration.create();

            if (modelType.equals("KANONYMITY")) {
                String k = model.item(0).getAttributes().item(0).getNodeValue();
                config.addPrivacyModel(new KAnonymity(Integer.parseInt(k)));
                config.setSuppressionLimit(Double.parseDouble(suppressionRateValue));
                config.setQualityModel(Metric.createLossMetric());
            } else {
                return Response.status(200)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Credentials", "true")
                        .header("Access-Control-Allow-Headers",
                                "origin, content-type, accept, authorization")
                        .header("Access-Control-Allow-Methods",
                                "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                        .entity("Privacy Model Not Supported!").build();
            }

            ARXAnonymizer anonymizer = new ARXAnonymizer();
            ARXResult result = anonymizer.anonymize(dataObj, config);

            DataHandle handle = result.getOutput();
            analyze(result, dataObj);

            String RESULT_PATH = path + "/ashe/Results/" + "output.csv";
            handle.save(RESULT_PATH, ',');

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .entity("Anonymization Completed!").build();
    }

    @GET
    @Path("/getResult")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getResult() {
        path = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        String RESULT_PATH = path + "/ashe/Results/" + "output.csv";

        File f = new File(RESULT_PATH);
        Response.ResponseBuilder response = Response.ok((Object) f);
        response.header("Content-Disposition", "attachment; filename=" + "output.csv");
        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .entity("Result Downloaded!").build();
    }

    protected static void analyze(final ARXResult result, final Data data) {
        final DecimalFormat df1 = new DecimalFormat("#####0.00");
        final String sTotal = df1.format(result.getTime() / 1000d) + "s";
        System.out.println(" - Time needed: " + sTotal);

        final ARXNode optimum = result.getGlobalOptimum();
        final List<String> qis = new ArrayList<String>(data.getDefinition().getQuasiIdentifyingAttributes());

        if (optimum == null) {
            System.out.println(" - No solution found!");
            return;
        }

        final StringBuffer[] identifiers = new StringBuffer[qis.size()];
        final StringBuffer[] generalizations = new StringBuffer[qis.size()];
        int lengthI = 0;
        int lengthG = 0;
        for (int i = 0; i < qis.size(); i++) {
            identifiers[i] = new StringBuffer();
            generalizations[i] = new StringBuffer();
            identifiers[i].append(qis.get(i));
            generalizations[i].append(optimum.getGeneralization(qis.get(i)));
            if (data.getDefinition().isHierarchyAvailable(qis.get(i)))
                generalizations[i].append("/").append(data.getDefinition().getHierarchy(qis.get(i))[0].length - 1);
            lengthI = Math.max(lengthI, identifiers[i].length());
            lengthG = Math.max(lengthG, generalizations[i].length());
        }

        for (int i = 0; i < qis.size(); i++) {
            while (identifiers[i].length() < lengthI) {
                identifiers[i].append(" ");
            }
            while (generalizations[i].length() < lengthG) {
                generalizations[i].insert(0, " ");
            }
        }

        System.out.println(" - Information loss: " + result.getGlobalOptimum().getLowestScore() + " / "
                + result.getGlobalOptimum().getHighestScore());
        System.out.println(" - Optimal generalization");
        for (int i = 0; i < qis.size(); i++) {
            System.out.println("   * " + identifiers[i] + ": " + generalizations[i]);
        }
        System.out.println(" - Statistics");
        System.out.println(
                result.getOutput(result.getGlobalOptimum(), false).getStatistics().getEquivalenceClassStatistics());
    }
}
