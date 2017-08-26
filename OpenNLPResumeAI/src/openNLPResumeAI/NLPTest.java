package openNLPResumeAI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
 
import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;

public class NLPTest {

	public static void main(String[] args) {
		 
		        // reading training data
		        InputStreamFactory in = null;
		        try {
		            in = new MarkableFileInputStreamFactory(new File("/Users/JayKesavan/Dropbox/Code/Open NLP/Models/AnnotatedSentences.txt"));
		        } catch (FileNotFoundException e2) {
		            e2.printStackTrace();
		        }
		        
		        ObjectStream<NameSample> sampleStream = null;
		        try {
		            sampleStream = new NameSampleDataStream(
		                new PlainTextByLineStream(in, StandardCharsets.UTF_8));
		        } catch (IOException e1) {
		            e1.printStackTrace();
		        }
		 
		        // setting the parameters for training
		        TrainingParameters params = new TrainingParameters();
		        params.put(TrainingParameters.ITERATIONS_PARAM, 70);
		        params.put(TrainingParameters.CUTOFF_PARAM, 1);
		 
		        // training the model using TokenNameFinderModel class
		        TokenNameFinderModel nameFinderModel = null;
		        try {
		            nameFinderModel = NameFinderME.train("en",null, sampleStream,
		                params, TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));
		            
		            /*TokenNameFinderModel nameFinderModel1 = NameFinderME.train("en", "default", sampleStream,
							(AdaptiveFeatureGenerator) null,
							Collections.<String, Object> emptyMap(), 70, 0);*/
		            
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        
		        // saving the model to "ner-custom-model.bin" file
		        try {
		            File output = new File("/Users/JayKesavan/Dropbox/Code/Open NLP/Models/ner-custom-model.bin");
		            FileOutputStream outputStream = new FileOutputStream(output);
		            nameFinderModel.serialize(outputStream);
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        
		        
		        // Loading name finder model into memory from disk
				InputStream modelIn = null;
				try {
					modelIn = new FileInputStream(
							"/Users/JayKesavan/Dropbox/Code/Open NLP/Models/ner-custom-model.bin");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				
		        // testing the model and printing the types it found in the input sentence
				TokenNameFinderModel model = null;
				try {
					model = new TokenNameFinderModel(modelIn);
					modelIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				
		        //TokenNameFinder nameFinder = new NameFinderME(nameFinderModel);
				TokenNameFinder nameFinder1 = new NameFinderME(model);				
		        String[] testSentence ={"Salesforce","from","Siebel", "Mulesoft", "Machine Learning"};
		 
		        System.out.println("Finding types in the test sentence..");
		        Span[] names = nameFinder1.find(testSentence);
		        System.out.println("Length of names:"+names.length);
		        for(Span name:names){
		            String personName="";
		            for(int i=name.getStart();i<name.getEnd();i++){
		                personName+=testSentence[i]+" ";
		            }
		            System.out.println(name.getType()+" : "+personName+"\t [probability="+name.getProb()+"]");
		        }
		    }
		 
	}


