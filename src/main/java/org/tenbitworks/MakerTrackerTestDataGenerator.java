package org.tenbitworks;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.tenbitworks.model.Asset;
import org.tenbitworks.model.AssetStatus;
import org.tenbitworks.model.Member;
import org.tenbitworks.model.MemberStatus;
import org.tenbitworks.model.MemberTrainings;
import org.tenbitworks.model.PaymentMethod;
import org.tenbitworks.model.Roles;
import org.tenbitworks.model.TrainingType;
import org.tenbitworks.repositories.AssetRepository;
import org.tenbitworks.repositories.MemberRepository;
import org.tenbitworks.repositories.MemberTrainingsRepository;
import org.tenbitworks.repositories.TrainingTypeRepository;
import org.tenbitworks.repositories.UserRepository;

@Configuration
public class MakerTrackerTestDataGenerator {
	private static final String CLAZZ = MakertrackerApplication.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLAZZ);
	
	@Value("${makertracker.testdata.generate:false}")
	boolean generateTestData;
	
	@Value("${makertracker.testdata.count:10}")
	int testDataCount;
	
	@Autowired
	UserDetailsManager userDetailsManager;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	MemberRepository memberRepo;
	
	@Autowired
	AssetRepository assetRepo;
	
	@Autowired
	TrainingTypeRepository trainingTypeRepo;
	
	@Autowired
	MemberTrainingsRepository memberTrainingRepo;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	Random random = new Random();
	
	@Bean
	@Order(1)
	public CommandLineRunner generateTestUsers() {
		return (args) -> {
			if (generateTestData && userRepo.count() <= 2) { //2 for admin and user defaults
				LOGGER.info("Generating Test Users");
				for (int i = 0; i < testDataCount; i++) {
					userDetailsManager.createUser(
							new User(
									"user" + i, 
									passwordEncoder.encode("user" + i), 
									Arrays.asList(new SimpleGrantedAuthority("ROLE_" + Roles.USER.toString()))));
				}
			}
		};
	}

	@Bean
	@Order(2)
	public CommandLineRunner generateMemberData() {
		return (args) -> {
			if (generateTestData && memberRepo.count() == 0) {
				LOGGER.info("Generating some members");
				
				for (int i = 0; i < testDataCount; i++) {
					LOGGER.info("Adding member " + i);
					Member newMember = new Member();
					newMember.setMemberName("member" + i);
					newMember.setAddress("address 1234 " + i);
					newMember.setDescription("member desc " + i);
					newMember.setEmail(i + "email@email.com");
					newMember.setPaymentMethod(PaymentMethod.PAYPAL);
					newMember.setPhoneNumber("210-123-4567");
					newMember.setRfid("rfid-badge-" + i);
					newMember.setStatus(MemberStatus.MEMBER);
					newMember.setZipCode("12345");
					newMember.setUser(userRepo.findOne("user" + i));
					
					memberRepo.save(newMember);
				}
			}
			
			LOGGER.info("Members found:");
			memberRepo.findAll().forEach(member -> LOGGER.info(member.toString()));
			LOGGER.info("-------------------------------");
		};
	}

	@Bean
	@Order(3)
	public CommandLineRunner generateAssetData() {
		return (args) -> {
			if (generateTestData && assetRepo.count() == 0) {
				LOGGER.info("Generating some assets");
				
				for (int i = 0; i < testDataCount; i++) {
					LOGGER.info("Adding asset " + i);
					Asset asset = new Asset();
					asset.setTenbitId("tenBitId" + i);
					asset.setBrand("brand" + i);
//					asset.setDateAcquired(dateAcquired);
					asset.setAccessControlTimeMS(random.nextInt(50000));
//					asset.setDateRemoved(dateRemoved);
					asset.setDescription("asset description " + i);
					asset.setDonor("donor " + i);
					asset.setModelNumber("model number " + i);
					asset.setOperator("Operator " + i);
					asset.setRetailValue(new BigDecimal(random.nextInt(1000000)));
					asset.setSerialNumber("serialNumber " + i);
					asset.setStatus(AssetStatus.OWNED);
					asset.setTitle("title " + i);
					asset.setTrainingRequired(random.nextBoolean());
					asset.setWebLink("http://url" + i);
					
					assetRepo.save(asset);
				}
			}
			
			LOGGER.info("Assets found:");
			assetRepo.findAll().forEach(asset -> LOGGER.info(asset.toString()));
			LOGGER.info("-------------------------------");
		};
	}
	
	@Bean
	@Order(4)
	public CommandLineRunner generateTrainingData() {
		return (args) -> {
			if (generateTestData && trainingTypeRepo.count() == 0) {
				LOGGER.info("Generating some TrainingTypes");
				
				for (int i = 0; i < testDataCount; i++) {
					LOGGER.info("Adding Training Type " + i);
					TrainingType tt = new TrainingType();
					tt.setName("TrainingType" + i);
					tt.setDescription("Training Description " + i);
					trainingTypeRepo.save(tt);
				}
			}
			
			LOGGER.info("TrainingTypes found:");
			trainingTypeRepo.findAll().forEach(trainingType -> LOGGER.info(trainingType.toString()));
			LOGGER.info("-------------------------------");
		};
	}
	
	@Bean
	@Order(5)
	public CommandLineRunner generateMemberTrainingData() {
		return (args) -> {
			if (generateTestData && memberTrainingRepo.count() == 0) {
				LOGGER.info("Generating some MemberTrainings");
				
				Iterator<Member> memberIt = memberRepo.findAll().iterator();
				TrainingType trainingType = trainingTypeRepo.findOne(1L);
				org.tenbitworks.model.User user = userRepo.findOne("admin");

				while (memberIt.hasNext()) {
					Member member = memberIt.next();
					LOGGER.info("Adding Member " + member.getMemberName() + " to Training Type " + trainingType.getId());
					MemberTrainings mt = new MemberTrainings();
					mt.setAddedBy(user);
					mt.setTrainingDate(Calendar.getInstance().getTime());
					mt.setTrainingType(trainingType);
					mt.setMember(member);
					memberTrainingRepo.save(mt);
				}
				
				Asset asset = assetRepo.findOneByTenbitId("tenBitId0"); 
				if (asset != null) {
					asset.setTrainingRequired(true);
					asset.setTrainingType(trainingType);
					asset.setAccessControlTimeMS(5000);
					assetRepo.save(asset);
				}
			}
			
			LOGGER.info("Trainings found:");
			memberTrainingRepo.findAll().forEach(training -> LOGGER.info(training.toString()));
			LOGGER.info("-------------------------------");
		};
	}
}
