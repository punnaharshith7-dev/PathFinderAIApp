package com.example.data.catalog

data class RoadmapModule(
    val id: String,
    val level: Int,
    val title: String,
    val subtitle: String,
    val durationWeeks: Int,
    val difficulty: String,
    val subtopics: List<String>,
    val objectives: List<String>,
    val videoResource: String,
    val docResource: String,
    val miniProject: String,
    val certification: String
)

data class CareerOption(
    val id: String,
    val title: String,
    val category: String,
    val baseMatchScore: Int,
    val tagline: String,
    val overview: String,
    val salaryRangeSample: String,
    val demandIndicator: String,
    val growthRateSample: String,
    val educationReq: String,
    val requiredTechSkills: Map<String, Int>,
    val requiredSoftSkills: List<String>,
    val dailyResponsibilities: List<String>,
    val careerProgression: List<String>,
    val recommendedCertifications: List<String>,
    val learningResources: List<String>,
    val whyItMatchesTemplate: String,
    val roadmap: List<RoadmapModule>
)

data class AssessmentQuestion(
    val id: String,
    val category: String,
    val prompt: String,
    val options: List<AssessmentOption>,
    val isMultiSelect: Boolean = false
)

data class AssessmentOption(
    val label: String,
    val dimensionWeights: Map<String, Int>
)

data class CertificationItem(
    val id: String,
    val title: String,
    val provider: String,
    val category: String,
    val difficulty: String,
    val duration: String,
    val skillsCovered: List<String>
)

data class InternshipItem(
    val id: String,
    val roleTitle: String,
    val companyType: String,
    val category: String,
    val stipendSample: String,
    val duration: String,
    val eligibility: String,
    val requiredSkills: List<String>,
    val description: String
)

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val category: String
)

data class InterviewQuestion(
    val id: String,
    val careerId: String,
    val roundType: String,
    val difficulty: String,
    val question: String,
    val keyTopics: List<String>,
    val sampleIdealPoints: List<String>
)

object CareerCatalog {

    private fun buildStandardRoadmap(
        roleTitle: String,
        lvl1: Pair<String, List<String>>,
        lvl2: Pair<String, List<String>>,
        lvl3: Pair<String, List<String>>,
        lvl4: Pair<String, List<String>>,
        lvl5: Pair<String, List<String>>,
        lvl6: Pair<String, List<String>>,
        lvl7: Pair<String, List<String>>,
        lvl8: Pair<String, List<String>>
    ): List<RoadmapModule> {
        val pairs = listOf(lvl1, lvl2, lvl3, lvl4, lvl5, lvl6, lvl7, lvl8)
        return pairs.mapIndexed { idx, (title, topics) ->
            val levelNum = idx + 1
            val diff = when {
                levelNum <= 3 -> "Beginner"
                levelNum <= 6 -> "Intermediate"
                else -> "Advanced"
            }
            RoadmapModule(
                id = "${roleTitle.lowercase().replace(" ", "_").replace("/", "_")}_lvl_$levelNum",
                level = levelNum,
                title = "Level $levelNum: $title",
                subtitle = "Core mastery for $roleTitle",
                durationWeeks = if (levelNum <= 3) 2 else 3,
                difficulty = diff,
                subtopics = topics,
                objectives = listOf(
                    "Master foundational concepts in $title",
                    "Build hands-on exercises and debug production scenarios",
                    "Complete the level capstone mini-project"
                ),
                videoResource = "$title Crash Course & Deep Dive Workshop",
                docResource = "Official $title Reference Guide & Best Practices",
                miniProject = "Build a production-grade $title module for your portfolio",
                certification = "$roleTitle - $title Verified Module"
            )
        } + listOf(
            RoadmapModule(
                id = "${roleTitle.lowercase().replace(" ", "_").replace("/", "_")}_lvl_9",
                level = 9,
                title = "Level 9: Real-World Capstone Projects",
                subtitle = "End-to-end portfolio architecture",
                durationWeeks = 4,
                difficulty = "Advanced",
                subtopics = listOf("System Design", "Testing & CI/CD", "Performance Profiling", "Documentation"),
                objectives = listOf("Ship 2 industry-grade portfolio projects", "Conduct peer code reviews"),
                videoResource = "Architecting Portfolio Projects That Stand Out",
                docResource = "Production Readiness Checklist",
                miniProject = "Full Capstone System for $roleTitle",
                certification = "Capstone Architecture Certificate"
            ),
            RoadmapModule(
                id = "${roleTitle.lowercase().replace(" ", "_").replace("/", "_")}_lvl_10",
                level = 10,
                title = "Level 10: Interview & Placement Preparation",
                subtitle = "Technical rounds, system design, and behavioral mastery",
                durationWeeks = 3,
                difficulty = "Advanced",
                subtopics = listOf("DSA & Problem Solving", "System Design Interviews", "STAR Behavioral Stories", "Resume Optimization"),
                objectives = listOf("Complete 3 Mock Interviews", "Achieve 85%+ Career Readiness Score"),
                videoResource = "Cracking Technical & Product Interviews",
                docResource = "Tech Interview Handbook & STAR Framework",
                miniProject = "Live Mock Interview & Portfolio Defense",
                certification = "Pathfinder AI Placement Ready Badge"
            )
        )
    }

    val careers: List<CareerOption> = listOf(
        CareerOption(
            id = "ai_ml_engineer",
            title = "AI/ML Engineer",
            category = "Emerging Technology",
            baseMatchScore = 94,
            tagline = "Design, train, and deploy intelligent machine learning & generative AI systems.",
            overview = "AI/ML Engineers bridge data science and software engineering to build production-ready predictive models, LLM pipelines, multimodal applications, and scalable inference systems.",
            salaryRangeSample = "$115k – $185k / ₹14L – ₹36L (Sample Estimate)",
            demandIndicator = "Very High Demand",
            growthRateSample = "+34% Projected 5-Yr Growth (Sample)",
            educationReq = "B.Tech / B.S. in CS, AI, Data Science, Mathematics, or equivalent portfolio",
            requiredTechSkills = mapOf(
                "Python" to 90,
                "Machine Learning" to 85,
                "Deep Learning & LLMs" to 80,
                "SQL" to 70,
                "Data Structures" to 80,
                "Git & Docker" to 75
            ),
            requiredSoftSkills = listOf("Analytical Thinking", "Problem Solving", "Research Mindset", "Communication"),
            dailyResponsibilities = listOf(
                "Fine-tune and evaluate foundation models and deep learning architectures",
                "Build low-latency inference pipelines and RAG architectures",
                "Collaborate with product teams to translate user problems into AI capabilities",
                "Monitor model drift, safety guardrails, and evaluation benchmarks"
            ),
            careerProgression = listOf("Junior ML Engineer", "AI/ML Engineer", "Senior Applied AI Scientist", "Principal AI Architect", "VP of AI"),
            recommendedCertifications = listOf(
                "Google Cloud Professional Machine Learning Engineer",
                "DeepLearning.AI TensorFlow & Generative AI Specialization",
                "AWS Certified Machine Learning – Specialty"
            ),
            learningResources = listOf("Fast.ai Practical Deep Learning", "Stanford CS229 & CS224N Notes", "Hugging Face LLM Course"),
            whyItMatchesTemplate = "Strong alignment with your Python & algorithmic foundation, analytical problem-solving score, and interest in Artificial Intelligence.",
            roadmap = buildStandardRoadmap(
                "AI/ML Engineer",
                "Python, Linear Algebra & Probability" to listOf("NumPy & Pandas", "Vector Calculus", "Probability Distributions", "Hypothesis Testing"),
                "Classical Machine Learning" to listOf("Supervised Learning", "Ensemble Methods (XGBoost)", "Clustering & PCA", "Feature Engineering"),
                "Deep Learning Foundations" to listOf("Neural Networks & Backprop", "PyTorch Fundamentals", "CNNs for Computer Vision", "Sequence Models"),
                "Transformers & Large Language Models" to listOf("Attention Mechanism", "Prompt Engineering", "Fine-Tuning (LoRA/QLoRA)", "Multimodal Models"),
                "RAG & Vector Databases" to listOf("Embeddings", "Vector Search", "Agentic Workflows", "Evaluation Frameworks"),
                "MLOps & Model Deployment" to listOf("FastAPI Model Serving", "Docker & Kubernetes", "Model Quantization", "Experiment Tracking"),
                "AI Safety, Ethics & Guardrails" to listOf("Bias Mitigation", "Adversarial Testing", "Latency & Cost Optimization", "Privacy Preserving ML"),
                "Distributed Training & System Design" to listOf("GPU Profiling", "Data Pipelines", "AI System Design Case Studies", "A/B Testing Models")
            )
        ),
        CareerOption(
            id = "full_stack_dev",
            title = "Full Stack Developer",
            category = "Technology",
            baseMatchScore = 91,
            tagline = "Architect complete web and mobile platforms from pixel-perfect UIs to distributed backends.",
            overview = "Full Stack Developers own the end-to-end product lifecycle—crafting responsive client applications, resilient REST/GraphQL APIs, relational/NoSQL schemas, and cloud deployments.",
            salaryRangeSample = "$95k – $160k / ₹10L – ₹28L (Sample Estimate)",
            demandIndicator = "Very High Demand",
            growthRateSample = "+26% Projected 5-Yr Growth (Sample)",
            educationReq = "B.Tech / B.E. / BCA / MCA or demonstrated full-stack portfolio",
            requiredTechSkills = mapOf(
                "JavaScript" to 85,
                "React" to 80,
                "Kotlin" to 75,
                "SQL" to 80,
                "Git & Docker" to 75,
                "Data Structures" to 75
            ),
            requiredSoftSkills = listOf("Problem Solving", "Teamwork", "Communication", "Adaptability"),
            dailyResponsibilities = listOf(
                "Develop responsive frontend interfaces and reusable design systems",
                "Design scalable backend microservices and REST/gRPC endpoints",
                "Optimize database queries, caching layers, and API latency",
                "Maintain automated tests and CI/CD deployment pipelines"
            ),
            careerProgression = listOf("Associate Full Stack Dev", "Full Stack Engineer", "Senior Product Engineer", "Staff Software Architect", "CTO"),
            recommendedCertifications = listOf(
                "Meta Full-Stack Engineer Professional Certificate",
                "AWS Certified Developer – Associate",
                "Google Associate Android / Cloud Developer"
            ),
            learningResources = listOf("Full Stack Open (University of Helsinki)", "MDN Web Docs", "System Design Primer"),
            whyItMatchesTemplate = "High compatibility with your Kotlin, JavaScript, and SQL proficiency and goal to build scalable user-facing products.",
            roadmap = buildStandardRoadmap(
                "Full Stack Developer",
                "Programming Fundamentals" to listOf("Data Structures & Algorithms", "Git & Version Control", "HTTP/HTTPS & Networking", "Linux CLI"),
                "HTML, CSS and JavaScript" to listOf("Semantic HTML5", "Modern CSS & Flexbox/Grid", "ES6+ JavaScript & TypeScript", "DOM & Async Programming"),
                "Frontend Development" to listOf("React & Component Architecture", "State Management", "Accessibility & Responsive Design", "Frontend Testing"),
                "Backend Development" to listOf("Node.js / Kotlin Ktor / Spring", "Routing & Middleware", "Concurrency & Async I/O", "Microservices Basics"),
                "Database Management" to listOf("PostgreSQL & Relational Modeling", "Indexing & Query Tuning", "NoSQL & Firestore", "Redis Caching"),
                "API Development" to listOf("RESTful API Design", "GraphQL & gRPC", "WebSockets & Real-Time Sync", "OpenAPI Documentation"),
                "Authentication and Security" to listOf("OAuth 2.0 & OIDC", "JWT & Session Security", "OWASP Top 10 Mitigation", "Rate Limiting & RBAC"),
                "Deployment and DevOps" to listOf("Docker Containers", "GitHub Actions CI/CD", "Cloud Hosting & CDN", "Observability & Logging")
            )
        ),
        CareerOption(
            id = "data_scientist",
            title = "Data Scientist",
            category = "Technology",
            baseMatchScore = 88,
            tagline = "Uncover actionable intelligence from complex datasets using statistics and machine learning.",
            overview = "Data Scientists combine statistical modeling, experimentation, and machine learning to guide high-stakes product, financial, and operational decisions.",
            salaryRangeSample = "$105k – $170k / ₹12L – ₹30L (Sample Estimate)",
            demandIndicator = "High Demand",
            growthRateSample = "+31% Projected 5-Yr Growth (Sample)",
            educationReq = "B.S. / M.S. in Data Science, Statistics, CS, Economics, or Engineering",
            requiredTechSkills = mapOf(
                "Python" to 85,
                "SQL" to 85,
                "Machine Learning" to 80,
                "Statistics & A/B Testing" to 85,
                "Data Visualization" to 75
            ),
            requiredSoftSkills = listOf("Analytical Thinking", "Communication", "Business Acumen", "Problem Solving"),
            dailyResponsibilities = listOf(
                "Formulate testable hypotheses and design rigorous A/B experiments",
                "Clean, explore, and model structured and unstructured datasets",
                "Build predictive customer churn, pricing, and recommendation models",
                "Present data-driven narratives to executive stakeholders"
            ),
            careerProgression = listOf("Associate Data Scientist", "Data Scientist", "Senior Data Scientist", "Lead Decision Scientist", "Chief Data Officer"),
            recommendedCertifications = listOf("IBM Data Science Professional Certificate", "Google Advanced Data Analytics", "Databricks Certified Associate"),
            learningResources = listOf("An Introduction to Statistical Learning (ISLR)", "Kaggle Competitions", "Mode SQL Tutorial"),
            whyItMatchesTemplate = "Aligns with your strong Python and SQL skills and high analytical thinking score.",
            roadmap = buildStandardRoadmap(
                "Data Scientist",
                "Exploratory Data Analysis & Python" to listOf("Pandas & Polars", "Data Cleaning", "Missing Value Strategies", "Outlier Detection"),
                "Advanced SQL & Data Warehousing" to listOf("Window Functions", "CTEs & Query Optimization", "BigQuery & Snowflake", "ETL Fundamentals"),
                "Probability & Statistical Inference" to listOf("Bayesian vs Frequentist", "Confidence Intervals", "Regression Analysis", "Causal Inference"),
                "Experimentation & A/B Testing" to listOf("Sample Size Calculation", "Variance Reduction", "Network Effects", "Multi-Armed Bandits"),
                "Predictive Modeling" to listOf("Tree-Based Models", "Time Series Forecasting", "Survival Analysis", "Model Explainability (SHAP)"),
                "Data Storytelling & Visualization" to listOf("Executive Dashboards", "Visual Perception Principles", "Metric Frameworks", "Narrative Memos"),
                "Big Data & Distributed Compute" to listOf("Apache Spark & PySpark", "Feature Stores", "Workflow Orchestration", "Data Governance"),
                "Production ML & Business Strategy" to listOf("ROI Estimation", "Model Monitoring", "Cross-Functional Alignment", "Case Study Synthesis")
            )
        ),
        CareerOption(
            id = "software_developer",
            title = "Software Developer",
            category = "Engineering",
            baseMatchScore = 89,
            tagline = "Build reliable, high-performance native mobile and cloud software systems.",
            overview = "Software Developers design, write, test, and maintain the core applications and systems that power modern consumer and enterprise computing.",
            salaryRangeSample = "$90k – $155k / ₹9L – ₹26L (Sample Estimate)",
            demandIndicator = "Very High Demand",
            growthRateSample = "+25% Projected 5-Yr Growth (Sample)",
            educationReq = "Bachelor's in Computer Science, Software Engineering, or equivalent",
            requiredTechSkills = mapOf(
                "Kotlin" to 85,
                "Data Structures" to 85,
                "Python" to 75,
                "SQL" to 70,
                "Git & Docker" to 75
            ),
            requiredSoftSkills = listOf("Problem Solving", "Teamwork", "Code Craftsmanship", "Communication"),
            dailyResponsibilities = listOf(
                "Write clean, modular, and well-tested Kotlin/Java/Python code",
                "Participate in architecture reviews and agile sprint planning",
                "Diagnose memory leaks, concurrency bugs, and performance bottlenecks",
                "Collaborate with designers and QA engineers to ship features"
            ),
            careerProgression = listOf("SDE I", "SDE II", "Senior Software Engineer", "Principal Engineer", "Distinguished Engineer"),
            recommendedCertifications = listOf("Associate Android Developer", "Oracle / JetBrains Kotlin Certification", "AWS Certified Developer"),
            learningResources = listOf("Effective Kotlin", "Clean Architecture by Robert C. Martin", "Android Developer Official Guides"),
            whyItMatchesTemplate = "Direct fit for your strong Kotlin & Data Structures foundation and CS engineering coursework.",
            roadmap = buildStandardRoadmap(
                "Software Developer",
                "Core Programming & OOP" to listOf("Kotlin Idioms", "SOLID Principles", "Generics & Collections", "Memory Management"),
                "Data Structures & Algorithms" to listOf("Trees & Graphs", "Dynamic Programming", "Hash Tables & Heaps", "Complexity Analysis"),
                "Modern App Architecture" to listOf("MVVM & Clean Architecture", "Dependency Injection", "Reactive Streams & Coroutines", "Modularization"),
                "UI Engineering & Jetpack Compose" to listOf("Declarative UI State", "Custom Layouts & Canvas", "Animations", "Accessibility"),
                "Local & Cloud Persistence" to listOf("SQLite & Room", "Firestore & Sync", "Caching Strategies", "Offline-First Design"),
                "Networking & Concurrency" to listOf("REST & Serialization", "Structured Concurrency", "WebSockets", "Security & TLS"),
                "Testing & Quality Assurance" to listOf("Unit Testing", "Robolectric JVM Tests", "UI Testing", "Static Analysis"),
                "Release Engineering & Performance" to listOf("R8/ProGuard Shrinking", "Baseline Profiles", "CI/CD Pipelines", "Crash Reporting")
            )
        ),
        CareerOption(
            id = "cloud_engineer",
            title = "Cloud Engineer",
            category = "Engineering",
            baseMatchScore = 84,
            tagline = "Design resilient, auto-scaling, and secure multi-cloud infrastructure.",
            overview = "Cloud Engineers architect and manage distributed cloud environments across Google Cloud, AWS, and Azure, ensuring 99.99% availability and cost efficiency.",
            salaryRangeSample = "$105k – $165k / ₹11L – ₹28L (Sample Estimate)",
            demandIndicator = "High Demand",
            growthRateSample = "+28% Projected 5-Yr Growth (Sample)",
            educationReq = "B.Tech / B.S. in CS, IT, or Cloud Certifications",
            requiredTechSkills = mapOf(
                "Git & Docker" to 85,
                "Linux & Networking" to 85,
                "Kubernetes" to 80,
                "Python" to 75,
                "Terraform (IaC)" to 80
            ),
            requiredSoftSkills = listOf("Problem Solving", "Decision Making", "Reliability Mindset", "Communication"),
            dailyResponsibilities = listOf(
                "Provision infrastructure as code using Terraform and Cloud Deployment Manager",
                "Manage Kubernetes clusters, service meshes, and serverless workloads",
                "Implement zero-trust IAM policies, VPC networking, and disaster recovery",
                "Optimize cloud spend and observability telemetry"
            ),
            careerProgression = listOf("Cloud Associate", "Cloud Infrastructure Engineer", "Senior Cloud Architect", "Principal Platform Architect"),
            recommendedCertifications = listOf("Google Cloud Associate Cloud Engineer", "AWS Certified Solutions Architect", "Certified Kubernetes Administrator (CKA)"),
            learningResources = listOf("Google Cloud Skills Boost", "Kubernetes Up & Running", "Site Reliability Engineering (Google SRE Book)"),
            whyItMatchesTemplate = "Matches your interest in Cloud & SaaS and growing Docker/infrastructure skills.",
            roadmap = buildStandardRoadmap(
                "Cloud Engineer",
                "Linux & Networking Internals" to listOf("DNS, TCP/IP & BGP", "Load Balancers & Proxies", "Linux Permissions & Systemd", "Bash Scripting"),
                "Core Cloud Services (GCP/AWS)" to listOf("Compute & Serverless", "Object & Block Storage", "VPC & Subnets", "IAM & Least Privilege"),
                "Containers & Docker" to listOf("Multi-Stage Dockerfiles", "Container Networking", "Image Security Scanning", "Container Registries"),
                "Kubernetes Orchestration" to listOf("Pods, Deployments & Services", "Helm Charts", "Ingress & Autoscaling", "StatefulSets"),
                "Infrastructure as Code" to listOf("Terraform Modules", "State Management", "Policy as Code", "GitOps with ArgoCD"),
                "CI/CD & DevSecOps" to listOf("Automated Build Pipelines", "Canary & Blue-Green Deploys", "Secrets Management", "Artifact Signing"),
                "Observability & SRE" to listOf("Prometheus & Grafana", "Distributed Tracing (OpenTelemetry)", "SLIs, SLOs & Error Budgets", "Incident Response"),
                "Multi-Cloud & FinOps Architecture" to listOf("High Availability Design", "Disaster Recovery Drills", "Cloud Cost Optimization", "Architecture Review")
            )
        ),
        CareerOption(
            id = "cybersecurity_analyst",
            title = "Cybersecurity Analyst",
            category = "Technology",
            baseMatchScore = 81,
            tagline = "Defend critical digital infrastructure, applications, and data from cyber threats.",
            overview = "Cybersecurity Analysts conduct threat modeling, vulnerability assessments, penetration testing, and incident response to safeguard organizations.",
            salaryRangeSample = "$95k – $160k / ₹10L – ₹27L (Sample Estimate)",
            demandIndicator = "Very High Demand",
            growthRateSample = "+32% Projected 5-Yr Growth (Sample)",
            educationReq = "B.Tech / B.S. in Cybersecurity, CS, or Security Certifications",
            requiredTechSkills = mapOf(
                "Network Security" to 85,
                "Linux & Networking" to 80,
                "Python" to 75,
                "Application Security" to 80,
                "SIEM & Threat Hunting" to 75
            ),
            requiredSoftSkills = listOf("Analytical Thinking", "Decision Making", "Attention to Detail", "Communication"),
            dailyResponsibilities = listOf(
                "Audit applications and cloud environments for OWASP and zero-day vulnerabilities",
                "Monitor SIEM alerts and orchestrate rapid incident containment",
                "Conduct red-team / blue-team exercises and code security reviews",
                "Design zero-trust authentication and encryption protocols"
            ),
            careerProgression = listOf("SOC Analyst", "Security Engineer", "Senior AppSec Architect", "Principal Threat Researcher", "CISO"),
            recommendedCertifications = listOf("CompTIA Security+", "Google Cybersecurity Professional Certificate", "OSCP / CEH"),
            learningResources = listOf("OWASP Web Security Testing Guide", "PortSwigger Web Security Academy", "TryHackMe & HackTheBox Labs"),
            whyItMatchesTemplate = "Strong fit for your analytical problem-solving mindset and structured systems thinking.",
            roadmap = buildStandardRoadmap(
                "Cybersecurity Analyst",
                "Networking & OS Security" to listOf("OSI Model & Packet Analysis", "Wireshark & TCPDump", "Linux Hardening", "Active Directory Basics"),
                "Cryptography & Identity" to listOf("Symmetric & Asymmetric Crypto", "PKI & TLS Handshakes", "OAuth2, OIDC & Passkeys", "Zero-Trust IAM"),
                "Web & Application Security" to listOf("OWASP Top 10", "SQLi, XSS & CSRF Defense", "API Security", "Secure Code Review"),
                "Network Defense & Firewalls" to listOf("IDS/IPS Systems", "VPNs & Zero Trust Network Access", "DDoS Mitigation", "Cloud Security Posture"),
                "Ethical Hacking & Pentesting" to listOf("Reconnaissance", "Vulnerability Scanning", "Privilege Escalation", "Reporting & Remediation"),
                "SIEM & Threat Intelligence" to listOf("Log Aggregation", "MITRE ATT&CK Framework", "Threat Hunting Queries", "SOAR Automation"),
                "Incident Response & Forensics" to listOf("Playbook Execution", "Memory & Disk Forensics", "Malware Triage", "Post-Mortem Analysis"),
                "Governance, Risk & Compliance" to listOf("ISO 27001 & SOC2", "Threat Modeling (STRIDE)", "Supply Chain Security", "Bug Bounty Defense")
            )
        ),
        CareerOption(
            id = "ui_ux_designer",
            title = "UI/UX Designer",
            category = "Creative",
            baseMatchScore = 83,
            tagline = "Craft intuitive, accessible, and visually magnetic digital product experiences.",
            overview = "UI/UX Designers blend human psychology, user research, interaction design, and design systems to transform complex workflows into delightful interfaces.",
            salaryRangeSample = "$85k – $145k / ₹8L – ₹24L (Sample Estimate)",
            demandIndicator = "High Demand",
            growthRateSample = "+22% Projected 5-Yr Growth (Sample)",
            educationReq = "B.Des / HCI / CS or strong interactive design portfolio",
            requiredTechSkills = mapOf(
                "Figma & Prototyping" to 90,
                "Design Systems" to 85,
                "User Research" to 80,
                "Interaction Design" to 85,
                "React" to 55
            ),
            requiredSoftSkills = listOf("Creative Thinking", "Empathy", "Communication", "Teamwork"),
            dailyResponsibilities = listOf(
                "Conduct user interviews, usability testing, and journey mapping",
                "Design high-fidelity wireframes, interactive prototypes, and micro-interactions",
                "Build and maintain scalable token-based design systems (Material 3 / iOS HIG)",
                "Partner closely with engineers to ensure design fidelity and accessibility"
            ),
            careerProgression = listOf("Product Designer I", "Senior UI/UX Designer", "Lead Design Systems Architect", "Director of Product Design", "VP of Design"),
            recommendedCertifications = listOf("Google UX Design Professional Certificate", "Nielsen Norman Group (NN/g) UX Certification", "IxDF Interaction Design"),
            learningResources = listOf("Refactoring UI", "The Design of Everyday Things", "Material Design 3 Guidelines"),
            whyItMatchesTemplate = "Complements your Product Design interest and strong communication & creative thinking scores.",
            roadmap = buildStandardRoadmap(
                "UI/UX Designer",
                "UX Foundations & Human Psychology" to listOf("Design Thinking", "Cognitive Load & Hick's Law", "Accessibility (WCAG)", "Information Architecture"),
                "User Research & Synthesis" to listOf("User Interviews", "Personas & Empathy Maps", "User Journey Mapping", "Competitive Audits"),
                "Wireframing & Interaction Flows" to listOf("Low-Fidelity Sketching", "Task Flows", "Navigation Patterns", "Edge Cases & Empty States"),
                "Visual Design Principles" to listOf("Typography Scales", "Color Theory & Contrast", "8pt Spatial Grids", "Visual Hierarchy"),
                "Figma Mastery & Auto-Layout" to listOf("Constraints & Auto-Layout", "Variables & Design Tokens", "Component Variants", "Responsive Breakpoints"),
                "Design Systems Architecture" to listOf("Material Design 3", "Multi-Theme Token Systems", "Component Documentation", "Developer Handoff"),
                "Interactive Prototyping & Motion" to listOf("Smart Animate", "Micro-Interactions", "Spring Physics", "Usability Testing Prototypes"),
                "Product Strategy & Case Studies" to listOf("Measuring UX Metrics (HEART)", "A/B Testing Designs", "Storytelling Case Studies", "Design Critiques")
            )
        ),
        CareerOption(
            id = "product_manager",
            title = "Product Manager",
            category = "Business",
            baseMatchScore = 86,
            tagline = "Define product vision, align cross-functional teams, and drive user & business impact.",
            overview = "Product Managers sit at the intersection of technology, design, and business—discovering customer pain points, prioritizing roadmaps, and shipping winning products.",
            salaryRangeSample = "$105k – $175k / ₹14L – ₹34L (Sample Estimate)",
            demandIndicator = "High Demand",
            growthRateSample = "+24% Projected 5-Yr Growth (Sample)",
            educationReq = "B.Tech / BBA / MBA or technical product experience",
            requiredTechSkills = mapOf(
                "Product Analytics & SQL" to 80,
                "System Architecture Basics" to 75,
                "Roadmapping & Prioritization" to 85,
                "AI Product Strategy" to 80
            ),
            requiredSoftSkills = listOf("Leadership", "Communication", "Decision Making", "Teamwork"),
            dailyResponsibilities = listOf(
                "Define product vision, North Star metrics, and quarterly OKRs",
                "Write crisp Product Requirement Documents (PRDs) and user stories",
                "Analyze funnel conversion, retention cohorts, and user feedback",
                "Lead cross-functional execution across engineering, design, and GTM teams"
            ),
            careerProgression = listOf("Associate Product Manager (APM)", "Product Manager", "Senior PM", "Group Product Manager", "Chief Product Officer (CPO)"),
            recommendedCertifications = listOf("Product School PMC", "Pragmatic Institute Product Management", "Reforge Product Strategy"),
            learningResources = listOf("Inspired by Marty Cagan", "Lenny's Newsletter", "Cracking the PM Interview"),
            whyItMatchesTemplate = "Strong match with your leadership, communication, and product-oriented career goals.",
            roadmap = buildStandardRoadmap(
                "Product Manager",
                "Product Thinking & Discovery" to listOf("Jobs-to-be-Done (JTBD)", "Problem vs Solution Space", "Market Sizing (TAM/SAM/SOM)", "User Discovery"),
                "Metrics & Product Analytics" to listOf("North Star Metrics", "Retention Cohorts", "Funnel Analysis", "SQL for Product Managers"),
                "Prioritization & Roadmapping" to listOf("RICE & Kano Frameworks", "Outcome-Based Roadmaps", "Writing PRDs", "Managing Technical Debt"),
                "UX & Design Collaboration" to listOf("Wireframing for PMs", "Usability Testing", "Onboarding Optimization", "Design Critiques"),
                "Technical Literacy for PMs" to listOf("APIs & System Architecture", "Database Tradeoffs", "AI/LLM Capabilities & Cost", "Agile & Scrum Ceremonies"),
                "Growth & Experimentation" to listOf("Product-Led Growth (PLG)", "A/B Testing Loops", "Viral Loops & Activation", "Pricing & Monetization"),
                "Stakeholder & Executive Leadership" to listOf("Influence Without Authority", "Executive Storytelling", "Go-To-Market Launches", "Cross-Team Alignment"),
                "PM Teardowns & Case Interviews" to listOf("Product Sense Cases", "Execution & Root Cause Cases", "Strategy Prompts", "APM Portfolio Teardowns")
            )
        ),
        CareerOption(
            id = "data_analyst",
            title = "Data Analyst",
            category = "Business",
            baseMatchScore = 82,
            tagline = "Transform raw business data into crystal-clear dashboards and strategic insights.",
            overview = "Data Analysts query, clean, and visualize operational and product datasets to answer critical business questions and spot growth opportunities.",
            salaryRangeSample = "$75k – $125k / ₹7L – ₹18L (Sample Estimate)",
            demandIndicator = "High Demand",
            growthRateSample = "+25% Projected 5-Yr Growth (Sample)",
            educationReq = "Bachelor's in any quantitative, business, or engineering discipline",
            requiredTechSkills = mapOf(
                "SQL" to 90,
                "Python" to 75,
                "BI Dashboards" to 85,
                "Statistics & A/B Testing" to 75
            ),
            requiredSoftSkills = listOf("Analytical Thinking", "Communication", "Attention to Detail", "Problem Solving"),
            dailyResponsibilities = listOf(
                "Write complex SQL queries to extract and aggregate business metrics",
                "Build interactive executive dashboards and automated reporting pipelines",
                "Identify revenue trends, cohort anomalies, and operational bottlenecks",
                "Collaborate with finance, marketing, and product teams on KPI tracking"
            ),
            careerProgression = listOf("Junior Data Analyst", "Data Analyst", "Senior BI Engineer", "Analytics Manager", "Director of Analytics"),
            recommendedCertifications = listOf("Google Data Analytics Professional Certificate", "Microsoft Power BI Data Analyst", "Tableau Desktop Specialist"),
            learningResources = listOf("SQLZoo & Mode SQL", "Storytelling with Data", "Python for Data Analysis by Wes McKinney"),
            whyItMatchesTemplate = "Matches your SQL & Python foundation and strong analytical problem-solving profile.",
            roadmap = buildStandardRoadmap(
                "Data Analyst",
                "Spreadsheets & Business Metrics" to listOf("Advanced Pivot Tables", "Unit Economics (CAC, LTV)", "Financial Modeling Basics", "Data Hygiene"),
                "SQL Mastery" to listOf("Joins & Aggregations", "Window Functions", "Subqueries & CTEs", "Cohort Queries"),
                "Python for Data Analysis" to listOf("Pandas DataFrames", "Automated Reporting", "Seaborn & Matplotlib", "API Data Fetching"),
                "Business Intelligence Tools" to listOf("Power BI / Tableau", "Data Modeling (Star Schema)", "Interactive Filters", "KPI Scorecards"),
                "Descriptive & Inferential Stats" to listOf("Distributions & Variance", "Correlation vs Causation", "Hypothesis Testing", "Forecasting Basics"),
                "Analytics Engineering (dbt)" to listOf("Data Transformation Pipelines", "Schema Testing", "Documentation", "Cloud Data Warehouses"),
                "Domain Analytics (Product & FinTech)" to listOf("Funnel & Churn Analysis", "Marketing Attribution", "Risk & Fraud Metrics", "Operations Analytics"),
                "Executive Storytelling" to listOf("Insight Decks", "Stakeholder Presentations", "Portfolio Case Studies", "SQL Live Coding Prep")
            )
        ),
        CareerOption(
            id = "devops_engineer",
            title = "DevOps Engineer",
            category = "Engineering",
            baseMatchScore = 80,
            tagline = "Automate software delivery, CI/CD pipelines, and high-velocity engineering workflows.",
            overview = "DevOps Engineers unite software development and systems operations through automation, continuous integration, container orchestration, and observability.",
            salaryRangeSample = "$100k – $165k / ₹11L – ₹28L (Sample Estimate)",
            demandIndicator = "High Demand",
            growthRateSample = "+27% Projected 5-Yr Growth (Sample)",
            educationReq = "B.Tech / B.S. in CS, IT, or strong systems engineering background",
            requiredTechSkills = mapOf(
                "Git & Docker" to 90,
                "Kubernetes" to 85,
                "Python" to 75,
                "Linux & Networking" to 85
            ),
            requiredSoftSkills = listOf("Problem Solving", "Teamwork", "Decision Making", "Adaptability"),
            dailyResponsibilities = listOf(
                "Build and optimize fast, deterministic CI/CD pipelines",
                "Automate container orchestration, scaling, and secret rotation",
                "Maintain monitoring, alerting, and log aggregation stacks",
                "Partner with developers to reduce build times and deployment friction"
            ),
            careerProgression = listOf("Junior DevOps Engineer", "DevOps Engineer", "Senior SRE / Platform Engineer", "Principal Infrastructure Architect"),
            recommendedCertifications = listOf("Docker & Kubernetes CKA", "AWS Certified DevOps Engineer", "HashiCorp Certified: Terraform Associate"),
            learningResources = listOf("The Phoenix Project", "DevOps Roadmap (roadmap.sh)", "Kubernetes Official Docs"),
            whyItMatchesTemplate = "Fits your Git & Docker experience and system architecture interests.",
            roadmap = buildStandardRoadmap(
                "DevOps Engineer",
                "Linux Systems & Scripting" to listOf("Shell & Python Automation", "Process & Memory Management", "Networking & Firewalls", "SSH & Security"),
                "Source Control & Build Systems" to listOf("Git Branching Strategies", "Gradle / npm / Poetry Builds", "Artifact Repositories", "Semantic Versioning"),
                "CI/CD Pipeline Architecture" to listOf("GitHub Actions & GitLab CI", "Automated Test Gates", "Matrix Builds", "Zero-Downtime Deployments"),
                "Containerization with Docker" to listOf("Multi-Stage Builds", "Docker Compose", "Distroless Images", "Vulnerability Scanning"),
                "Kubernetes in Production" to listOf("Cluster Architecture", "RBAC & Network Policies", "Helm & Kustomize", "Horizontal Pod Autoscaling"),
                "Infrastructure as Code" to listOf("Terraform Providers", "Modular Infrastructure", "Ansible Configuration", "Drift Detection"),
                "Observability & Incident Management" to listOf("Prometheus Metrics", "Grafana Dashboards", "ELK / Loki Logs", "On-Call Runbooks"),
                "Platform Engineering" to listOf("Internal Developer Portals", "Golden Paths", "FinOps & Cost Controls", "Chaos Engineering")
            )
        )
    )

    val assessmentQuestions: List<AssessmentQuestion> = listOf(
        AssessmentQuestion(
            id = "q1_tech",
            category = "Technical Interests",
            prompt = "Which technical challenge excites you the most when starting a new project?",
            options = listOf(
                AssessmentOption("Training an AI model or intelligent recommendation engine", mapOf("AI" to 30, "Analytical" to 20, "Tech" to 25)),
                AssessmentOption("Building a complete interactive web or mobile product end-to-end", mapOf("FullStack" to 30, "Tech" to 25, "Creative" to 15)),
                AssessmentOption("Designing cloud infrastructure, security, or high-scale backend systems", mapOf("Cloud" to 30, "Security" to 25, "Analytical" to 20)),
                AssessmentOption("Crafting pixel-perfect interfaces and smooth user experiences", mapOf("Design" to 30, "Creative" to 30, "Communication" to 15))
            )
        ),
        AssessmentQuestion(
            id = "q2_creative",
            category = "Creative Thinking",
            prompt = "When solving an open-ended problem, how do you prefer to approach ideation?",
            options = listOf(
                AssessmentOption("Sketching visual wireframes, user journeys, and interactive prototypes", mapOf("Creative" to 30, "Design" to 25)),
                AssessmentOption("Experimenting with novel algorithms and combining data sources creatively", mapOf("AI" to 25, "Analytical" to 25, "Creative" to 20)),
                AssessmentOption("Brainstorming product strategy, positioning, and user value propositions", mapOf("Leadership" to 25, "Business" to 30, "Creative" to 20)),
                AssessmentOption("Designing clean, modular code architectures and reusable abstractions", mapOf("Tech" to 30, "FullStack" to 25))
            )
        ),
        AssessmentQuestion(
            id = "q3_analytical",
            category = "Analytical Thinking",
            prompt = "How do you feel about working with complex datasets, probabilities, and metrics?",
            options = listOf(
                AssessmentOption("I love diving deep into statistical models, SQL queries, and pattern discovery", mapOf("Analytical" to 35, "AI" to 25)),
                AssessmentOption("I enjoy using metrics and A/B tests to guide product and engineering decisions", mapOf("Analytical" to 25, "Business" to 25, "FullStack" to 15)),
                AssessmentOption("I prefer analyzing system logs, latency metrics, and security vulnerabilities", mapOf("Cloud" to 25, "Security" to 30, "Analytical" to 20)),
                AssessmentOption("I prefer qualitative user research paired with high-level summary metrics", mapOf("Design" to 25, "Communication" to 20))
            )
        ),
        AssessmentQuestion(
            id = "q4_problem_solving",
            category = "Problem Solving",
            prompt = "A critical system breaks 2 hours before a major demo. What is your natural instinct?",
            options = listOf(
                AssessmentOption("Isolate the root cause systematically using logs, breakpoints, and binary search", mapOf("Tech" to 30, "Analytical" to 25, "Cloud" to 20)),
                AssessmentOption("Coordinate the team, prioritize a safe fallback, and manage stakeholder communication", mapOf("Leadership" to 35, "Business" to 25, "Communication" to 25)),
                AssessmentOption("Check data pipelines, model inputs, and edge-case payloads for anomalies", mapOf("AI" to 25, "Analytical" to 30)),
                AssessmentOption("Redesign the user flow quickly so the experience remains seamless despite the issue", mapOf("Design" to 25, "Creative" to 25, "FullStack" to 20))
            )
        ),
        AssessmentQuestion(
            id = "q5_leadership",
            category = "Leadership",
            prompt = "In a university hackathon or team project, which role do you naturally gravitate toward?",
            options = listOf(
                AssessmentOption("Product & Team Lead: defining scope, aligning everyone, and pitching the vision", mapOf("Leadership" to 35, "Business" to 30, "Communication" to 25)),
                AssessmentOption("Lead Architect: setting up the repo, core backend/AI engine, and unblocking coders", mapOf("Tech" to 30, "FullStack" to 25, "Leadership" to 20)),
                AssessmentOption("AI / Data Specialist: building the smart differentiating model or analytics engine", mapOf("AI" to 35, "Analytical" to 25)),
                AssessmentOption("Design & Frontend Lead: making the product look and feel like a funded startup", mapOf("Design" to 30, "Creative" to 30, "FullStack" to 20))
            )
        ),
        AssessmentQuestion(
            id = "q6_communication",
            category = "Communication",
            prompt = "Which communication task gives you the highest sense of accomplishment?",
            options = listOf(
                AssessmentOption("Presenting a compelling product demo or strategy deck to judges and executives", mapOf("Communication" to 35, "Leadership" to 25, "Business" to 25)),
                AssessmentOption("Writing crystal-clear technical documentation, RFCs, and system architecture diagrams", mapOf("Tech" to 25, "Cloud" to 20, "Communication" to 25)),
                AssessmentOption("Translating complex statistical findings into clear visual charts and stories", mapOf("Analytical" to 30, "Communication" to 25, "AI" to 15)),
                AssessmentOption("Conducting empathetic user interviews to uncover hidden usability pain points", mapOf("Design" to 30, "Communication" to 25, "Creative" to 20))
            )
        ),
        AssessmentQuestion(
            id = "q7_teamwork",
            category = "Teamwork",
            prompt = "What type of cross-functional collaboration energizes you the most?",
            options = listOf(
                AssessmentOption("Pairing with engineers and data scientists to ship an AI-powered feature", mapOf("AI" to 25, "FullStack" to 25, "Tech" to 20)),
                AssessmentOption("Bridging design, engineering, and business to deliver a cohesive roadmap", mapOf("Business" to 30, "Leadership" to 25, "Communication" to 20)),
                AssessmentOption("Working with designers and frontend devs to polish every micro-interaction", mapOf("Design" to 30, "FullStack" to 20, "Creative" to 20)),
                AssessmentOption("Collaborating with platform and security teams to harden production reliability", mapOf("Cloud" to 30, "Security" to 25, "Tech" to 20))
            )
        ),
        AssessmentQuestion(
            id = "q8_decision_making",
            category = "Decision Making",
            prompt = "When choosing between two competing technical or product directions, how do you decide?",
            options = listOf(
                AssessmentOption("Run an experiment or benchmark and let empirical data decide", mapOf("Analytical" to 30, "AI" to 25)),
                AssessmentOption("Evaluate long-term maintainability, scalability, and engineering tradeoffs", mapOf("Tech" to 30, "Cloud" to 25, "FullStack" to 20)),
                AssessmentOption("Weigh customer impact against time-to-market and strategic goals", mapOf("Business" to 30, "Leadership" to 25)),
                AssessmentOption("Prototype both quickly and test which one feels more intuitive to real users", mapOf("Design" to 30, "Creative" to 25))
            )
        ),
        AssessmentQuestion(
            id = "q9_environment",
            category = "Work Environment Preferences",
            prompt = "What kind of work environment helps you perform at your peak?",
            options = listOf(
                AssessmentOption("High-velocity AI/tech startup building cutting-edge products from 0 to 1", mapOf("FullStack" to 25, "AI" to 25, "Creative" to 20)),
                AssessmentOption("R&D or applied AI lab solving deep algorithmic and modeling challenges", mapOf("AI" to 35, "Analytical" to 30)),
                AssessmentOption("Global technology company with massive scale, mentorship, and structured systems", mapOf("Tech" to 25, "Cloud" to 25, "Security" to 20)),
                AssessmentOption("Product-led studio or strategy team focused on user experience and growth", mapOf("Design" to 25, "Business" to 25, "Leadership" to 20))
            )
        ),
        AssessmentQuestion(
            id = "q10_industries",
            category = "Preferred Industries",
            prompt = "Which industry domain inspires you the most for your early career?",
            options = listOf(
                AssessmentOption("Artificial Intelligence, Foundation Models & Developer Tools", mapOf("AI" to 35, "Tech" to 25)),
                AssessmentOption("Consumer Tech, FinTech, & Scalable SaaS Platforms", mapOf("FullStack" to 30, "Business" to 20, "Tech" to 20)),
                AssessmentOption("Cloud Infrastructure, Cybersecurity & Enterprise Systems", mapOf("Cloud" to 30, "Security" to 30)),
                AssessmentOption("Interactive Media, EdTech, Design & Digital Consumer Products", mapOf("Design" to 30, "Creative" to 25, "Business" to 15))
            )
        ),
        AssessmentQuestion(
            id = "q11_personality",
            category = "Personality Preferences",
            prompt = "Which statement best describes your core professional superpower?",
            options = listOf(
                AssessmentOption("Curiosity & Quantitative Rigor: I question assumptions and prove them with data", mapOf("Analytical" to 30, "AI" to 25)),
                AssessmentOption("Builder's Momentum: I turn ideas into working apps and APIs rapidly", mapOf("FullStack" to 35, "Tech" to 25)),
                AssessmentOption("Strategic Vision & Empathy: I rally people around user-centric outcomes", mapOf("Leadership" to 30, "Business" to 25, "Design" to 20)),
                AssessmentOption("Guardian of Quality: I anticipate edge cases, security flaws, and scale bottlenecks", mapOf("Security" to 30, "Cloud" to 25, "Tech" to 20))
            )
        ),
        AssessmentQuestion(
            id = "q12_aptitude",
            category = "Aptitude",
            prompt = "When learning a complex new technology or subject, how do you master it fastest?",
            options = listOf(
                AssessmentOption("Reading research papers, mathematical proofs, and running notebooks", mapOf("AI" to 30, "Analytical" to 30)),
                AssessmentOption("Building a full-featured side project from scratch and deploying it live", mapOf("FullStack" to 30, "Tech" to 30)),
                AssessmentOption("Deconstructing how top products work, their UX flows, and business models", mapOf("Design" to 25, "Business" to 30, "Leadership" to 20)),
                AssessmentOption("Setting up labs, containers, and stress-testing how the system behaves under load", mapOf("Cloud" to 30, "Security" to 25, "Tech" to 20))
            )
        )
    )

    val certifications: List<CertificationItem> = listOf(
        CertificationItem(
            id = "cert_gcp_ml",
            title = "Google Cloud Professional ML Engineer",
            provider = "Google Cloud",
            category = "AI & Machine Learning",
            difficulty = "Advanced",
            duration = "8 Weeks",
            skillsCovered = listOf("Vertex AI", "TensorFlow", "MLOps", "Model Deployment")
        ),
        CertificationItem(
            id = "cert_deeplearning_genai",
            title = "Generative AI with Large Language Models",
            provider = "DeepLearning.AI & AWS",
            category = "AI & Machine Learning",
            difficulty = "Intermediate",
            duration = "4 Weeks",
            skillsCovered = listOf("Transformers", "Fine-Tuning", "RLHF", "RAG")
        ),
        CertificationItem(
            id = "cert_meta_fullstack",
            title = "Full-Stack Engineer Professional Certificate",
            provider = "Meta",
            category = "Software Engineering",
            difficulty = "Intermediate",
            duration = "10 Weeks",
            skillsCovered = listOf("React", "JavaScript", "APIs", "SQL", "UI/UX")
        ),
        CertificationItem(
            id = "cert_google_android",
            title = "Associate Android Developer (Kotlin & Compose)",
            provider = "Google Developers",
            category = "Software Engineering",
            difficulty = "Intermediate",
            duration = "6 Weeks",
            skillsCovered = listOf("Kotlin", "Jetpack Compose", "Coroutines", "App Architecture")
        ),
        CertificationItem(
            id = "cert_aws_saa",
            title = "AWS Certified Solutions Architect – Associate",
            provider = "Amazon Web Services",
            category = "Cloud & DevOps",
            difficulty = "Intermediate",
            duration = "6 Weeks",
            skillsCovered = listOf("VPC", "IAM", "Serverless", "High Availability")
        ),
        CertificationItem(
            id = "cert_cka",
            title = "Certified Kubernetes Administrator (CKA)",
            provider = "Cloud Native Computing Foundation",
            category = "Cloud & DevOps",
            difficulty = "Advanced",
            duration = "8 Weeks",
            skillsCovered = listOf("Kubernetes", "Containers", "Cluster Networking", "Security")
        ),
        CertificationItem(
            id = "cert_google_ux",
            title = "Google UX Design Professional Certificate",
            provider = "Google",
            category = "Design & Product",
            difficulty = "Beginner",
            duration = "6 Weeks",
            skillsCovered = listOf("Figma", "User Research", "Wireframing", "Design Systems")
        ),
        CertificationItem(
            id = "cert_comptia_sec",
            title = "CompTIA Security+ (SY0-701)",
            provider = "CompTIA",
            category = "Cybersecurity",
            difficulty = "Intermediate",
            duration = "6 Weeks",
            skillsCovered = listOf("Threat Mitigation", "Cryptography", "Zero Trust", "Incident Response")
        )
    )

    val internships: List<InternshipItem> = listOf(
        InternshipItem(
            id = "intern_ai_research",
            roleTitle = "Applied AI / LLM Engineering Intern (Sample)",
            companyType = "AI Research & Product Startup",
            category = "AI & Data",
            stipendSample = "$3,500/mo or ₹60,000/mo (Sample Listing)",
            duration = "3 – 6 Months (Remote / Hybrid)",
            eligibility = "3rd/4th Year B.Tech/M.Tech in CS, AI, or related fields",
            requiredSkills = listOf("Python", "PyTorch", "LLM Prompting", "RAG Pipelines"),
            description = "Sample internship role focused on evaluating multimodal prompts, building retrieval pipelines, and benchmarking latency."
        ),
        InternshipItem(
            id = "intern_fullstack",
            roleTitle = "Full Stack Product Engineering Intern (Sample)",
            companyType = "High-Growth FinTech SaaS",
            category = "Software Engineering",
            stipendSample = "$3,000/mo or ₹50,000/mo (Sample Listing)",
            duration = "6 Months",
            eligibility = "Pre-final or final year undergraduate students",
            requiredSkills = listOf("React", "TypeScript", "Kotlin / Node.js", "PostgreSQL"),
            description = "Sample internship role building real-time customer dashboards, API integrations, and automated test suites."
        ),
        InternshipItem(
            id = "intern_android",
            roleTitle = "Mobile Engineer Intern – Kotlin & Compose (Sample)",
            companyType = "Consumer EdTech Platform",
            category = "Software Engineering",
            stipendSample = "$2,800/mo or ₹45,000/mo (Sample Listing)",
            duration = "3 Months (Summer Cohort)",
            eligibility = "2nd/3rd/4th Year CS/IT students with Android portfolio",
            requiredSkills = listOf("Kotlin", "Jetpack Compose", "Material 3", "Coroutines"),
            description = "Sample internship crafting smooth animations, offline-first study modules, and adaptive tablet layouts."
        ),
        InternshipItem(
            id = "intern_data_science",
            roleTitle = "Data Science & Decision Analytics Intern (Sample)",
            companyType = "Global E-Commerce Marketplace",
            category = "AI & Data",
            stipendSample = "$3,200/mo or ₹55,000/mo (Sample Listing)",
            duration = "4 – 6 Months",
            eligibility = "B.S./M.S. in Data Science, Statistics, CS, or Economics",
            requiredSkills = listOf("SQL", "Python", "A/B Testing", "Machine Learning"),
            description = "Sample internship analyzing search ranking conversion, building churn prediction models, and designing experiment dashboards."
        ),
        InternshipItem(
            id = "intern_product_design",
            roleTitle = "Product Design (UI/UX) Intern (Sample)",
            companyType = "Design-Led Productivity Startup",
            category = "Design & Product",
            stipendSample = "$2,500/mo or ₹40,000/mo (Sample Listing)",
            duration = "3 Months",
            eligibility = "Design or HCI students with a strong Figma case study portfolio",
            requiredSkills = listOf("Figma", "Design Systems", "Prototyping", "User Research"),
            description = "Sample internship contributing to a multi-platform design system, conducting usability tests, and shipping mobile flows."
        ),
        InternshipItem(
            id = "intern_cloud_sec",
            roleTitle = "Cloud Infrastructure & DevSecOps Intern (Sample)",
            companyType = "Enterprise Cloud Security Provider",
            category = "Cloud & Security",
            stipendSample = "$3,100/mo or ₹48,000/mo (Sample Listing)",
            duration = "6 Months",
            eligibility = "3rd/4th Year Engineering students familiar with Linux & Docker",
            requiredSkills = listOf("Docker", "Kubernetes", "Linux", "Terraform"),
            description = "Sample internship automating CI/CD security scans, hardening container images, and managing cloud observability."
        )
    )

    val badges: List<AchievementBadge> = listOf(
        AchievementBadge("badge_first_assessment", "First Assessment Completed", "Completed the 12-dimension Career & Personality Assessment.", 150, "Assessment"),
        AchievementBadge("badge_career_explorer", "Career Explorer", "Explored and bookmarked 3+ high-match career paths.", 100, "Exploration"),
        AchievementBadge("badge_roadmap_starter", "Roadmap Starter", "Completed the first 3 levels of your personalized learning roadmap.", 200, "Learning"),
        AchievementBadge("badge_streak_7", "7-Day Learning Streak", "Maintained consistent daily career preparation for 7 consecutive days.", 250, "Consistency"),
        AchievementBadge("badge_skill_master", "Skill Master", "Reached 80%+ proficiency in at least 3 core technical or soft skills.", 300, "Skills"),
        AchievementBadge("badge_interview_ready", "Interview Ready", "Completed a full AI Mock Interview session with detailed feedback.", 250, "Interview"),
        AchievementBadge("badge_resume_completed", "Resume Completed", "Analyzed and optimized your resume in the Pathfinder Resume Lab.", 200, "Resume")
    )

    val interviewQuestions: List<InterviewQuestion> = listOf(
        InterviewQuestion(
            id = "iq_ai_1",
            careerId = "ai_ml_engineer",
            roundType = "Technical",
            difficulty = "Intermediate",
            question = "How would you design a Retrieval-Augmented Generation (RAG) pipeline to minimize hallucinations on university course documents?",
            keyTopics = listOf("Chunking Strategy", "Hybrid Search", "Reranking", "Citation Grounding"),
            sampleIdealPoints = listOf(
                "Use semantic/structural chunking with overlap to preserve context boundaries.",
                "Combine dense vector embeddings with BM25 keyword search (hybrid retrieval).",
                "Apply a cross-encoder reranker and strict system prompt instructing the model to cite chunk IDs or abstain."
            )
        ),
        InterviewQuestion(
            id = "iq_ai_2",
            careerId = "ai_ml_engineer",
            roundType = "Technical",
            difficulty = "Advanced",
            question = "Explain the bias-variance tradeoff and how you diagnose overfitting vs underfitting during deep neural network training.",
            keyTopics = listOf("Training vs Validation Loss", "Regularization", "Dropout & Weight Decay", "Data Augmentation"),
            sampleIdealPoints = listOf(
                "Compare training vs validation learning curves across epochs.",
                "Mitigate high variance (overfitting) using weight decay (L2), dropout, early stopping, or data augmentation.",
                "Address high bias (underfitting) by increasing model capacity, feature richness, or training duration."
            )
        ),
        InterviewQuestion(
            id = "iq_fs_1",
            careerId = "full_stack_dev",
            roundType = "Technical",
            difficulty = "Intermediate",
            question = "Walk through how you design an idempotent REST API endpoint for processing student course enrollments or payments.",
            keyTopics = listOf("Idempotency Keys", "Database Transactions", "Concurrency Control", "Retry Safety"),
            sampleIdealPoints = listOf(
                "Require a client-generated `Idempotency-Key` header stored in a unique database index or Redis with TTL.",
                "Wrap state checks and writes inside an ACID database transaction with optimistic or pessimistic locking.",
                "Return the cached response safely if a duplicate request arrives during network retries."
            )
        ),
        InterviewQuestion(
            id = "iq_behav_1",
            careerId = "all",
            roundType = "Behavioral (STAR)",
            difficulty = "Intermediate",
            question = "Tell me about a time when you had to learn a completely unfamiliar technology or framework under a tight deadline. How did you approach it?",
            keyTopics = listOf("Situation & Task", "Structured Learning Action", "Execution & Measurable Result"),
            sampleIdealPoints = listOf(
                "Clearly state the project context and why the new technology was necessary (Situation/Task).",
                "Describe how you focused on the core 20% primitives, built a vertical slice prototype, and consulted official docs (Action).",
                "Quantify the final outcome—shipping on time, performance gains, or lessons shared with the team (Result)."
            )
        ),
        InterviewQuestion(
            id = "iq_hr_1",
            careerId = "all",
            roundType = "HR & Career Goals",
            difficulty = "Beginner",
            question = "Why are you interested in this role, and what kind of engineering or product impact do you want to make in your first 6 months?",
            keyTopics = listOf("Role Alignment", "First 90-Day Ownership", "Team Collaboration"),
            sampleIdealPoints = listOf(
                "Connect your hands-on projects and assessment strengths directly to the role's core challenges.",
                "Outline a realistic 30-60-90 day plan: onboarding into the codebase, shipping early bugfixes/features, then owning a module.",
                "Highlight curiosity, code quality, and proactive communication."
            )
        )
    )
}
