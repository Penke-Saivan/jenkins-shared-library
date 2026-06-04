# 🚀 Jenkins Shared Library for CI/CD (EKS Deployment)

## 📌 Overview

This project implements a **Jenkins Shared Library** to standardize and reuse CI/CD pipelines across multiple applications.

---

## 🧠 Architecture

```
Application Repo (Jenkinsfile)
        ↓
Jenkins Shared Library
        ↓
Reusable Pipelines
   - nodeJSEKSPipeline (CI)
   - EKSDeploy (CD)
```

---

## 📂 Structure

```id="jen1"
jenkins-shared-library/
├── vars/
│   ├── nodeJSEKSPipeline.groovy
│   └── EKSDeploy.groovy
```

---

## ⚙️ CI Pipeline (nodeJSEKSPipeline)

### Features

* Build & dependency installation
* Unit testing
* Version extraction from package.json
* Docker build & push to AWS ECR
* Security checks (SonarQube, Trivy, Dependabot)
* Trigger downstream deployment

---

## 🚀 CD Pipeline (EKSDeploy)

### Features

* Connect to EKS cluster
* Deploy using Helm
* Environment-based deployments
* Automatic rollback using Helm

---

## 🔄 Deployment Flow

1. Developer pushes code
2. CI pipeline runs
3. Docker image built & pushed
4. Deployment pipeline triggered
5. Helm deploys application to EKS

---

## 🔐 Security & Quality

* SonarQube for code quality
* Trivy for vulnerability scanning
* Dependabot for dependency checks

---

## ⚡ Benefits

* Reusable pipelines
* Reduced duplication
* Standardized CI/CD
* Faster onboarding of new services

---

## 👨‍💻 Author

Sai Pavan – DevOps Engineer
