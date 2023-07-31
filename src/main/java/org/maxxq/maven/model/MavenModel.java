package org.maxxq.maven.model;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Build;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.Scm;

public class MavenModel extends Model {
    private static final long serialVersionUID = 1132086023761317234L;
    private final Date        creationDate;
    private final Model       model;

    public MavenModel( Model model, Date creationDate ) {
        super();
        this.model = model;
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int hashCode() {
        return model.hashCode();
    }

    public void addDependency( Dependency dependency ) {
        model.addDependency( dependency );
    }

    public void addModule( String string ) {
        model.addModule( string );
    }

    public void addPluginRepository( Repository repository ) {
        model.addPluginRepository( repository );
    }

    public boolean equals( Object obj ) {
        return model.equals( obj );
    }

    public void addProperty( String key, String value ) {
        model.addProperty( key, value );
    }

    public void addRepository( Repository repository ) {
        model.addRepository( repository );
    }

    public void addContributor( Contributor contributor ) {
        model.addContributor( contributor );
    }

    public void addDeveloper( Developer developer ) {
        model.addDeveloper( developer );
    }

    public void addLicense( License license ) {
        model.addLicense( license );
    }

    public void addMailingList( MailingList mailingList ) {
        model.addMailingList( mailingList );
    }

    public void addProfile( Profile profile ) {
        model.addProfile( profile );
    }

    public org.apache.maven.model.Model clone() {
        return model.clone();
    }

    public List<Dependency> getDependencies() {
        return model.getDependencies();
    }

    public DependencyManagement getDependencyManagement() {
        return model.getDependencyManagement();
    }

    public DistributionManagement getDistributionManagement() {
        return model.getDistributionManagement();
    }

    public InputLocation getLocation( Object key ) {
        return model.getLocation( key );
    }

    public String getArtifactId() {
        return model.getArtifactId();
    }

    public Build getBuild() {
        return model.getBuild();
    }

    public String getChildProjectUrlInheritAppendPath() {
        return model.getChildProjectUrlInheritAppendPath();
    }

    public List<String> getModules() {
        return model.getModules();
    }

    public void setLocation( Object key, InputLocation location ) {
        model.setLocation( key, location );
    }

    public CiManagement getCiManagement() {
        return model.getCiManagement();
    }

    public List<Contributor> getContributors() {
        return model.getContributors();
    }

    public String getDescription() {
        return model.getDescription();
    }

    public List<Developer> getDevelopers() {
        return model.getDevelopers();
    }

    public String getGroupId() {
        return model.getGroupId();
    }

    public void setOtherLocation( Object key, InputLocation location ) {
        model.setOtherLocation( key, location );
    }

    public String getInceptionYear() {
        return model.getInceptionYear();
    }

    public IssueManagement getIssueManagement() {
        return model.getIssueManagement();
    }

    public List<License> getLicenses() {
        return model.getLicenses();
    }

    public List<Repository> getPluginRepositories() {
        return model.getPluginRepositories();
    }

    public List<MailingList> getMailingLists() {
        return model.getMailingLists();
    }

    public Properties getProperties() {
        return model.getProperties();
    }

    public String getModelEncoding() {
        return model.getModelEncoding();
    }

    public Reporting getReporting() {
        return model.getReporting();
    }

    public String getModelVersion() {
        return model.getModelVersion();
    }

    public String getName() {
        return model.getName();
    }

    public Object getReports() {
        return model.getReports();
    }

    public Organization getOrganization() {
        return model.getOrganization();
    }

    public List<Repository> getRepositories() {
        return model.getRepositories();
    }

    public String getPackaging() {
        return model.getPackaging();
    }

    public void removeDependency( Dependency dependency ) {
        model.removeDependency( dependency );
    }

    public void removeModule( String string ) {
        model.removeModule( string );
    }

    public Parent getParent() {
        return model.getParent();
    }

    public void removePluginRepository( Repository repository ) {
        model.removePluginRepository( repository );
    }

    public void removeRepository( Repository repository ) {
        model.removeRepository( repository );
    }

    public Prerequisites getPrerequisites() {
        return model.getPrerequisites();
    }

    public void setDependencies( List<Dependency> dependencies ) {
        model.setDependencies( dependencies );
    }

    public List<Profile> getProfiles() {
        return model.getProfiles();
    }

    public Scm getScm() {
        return model.getScm();
    }

    public String getUrl() {
        return model.getUrl();
    }

    public void setDependencyManagement( DependencyManagement dependencyManagement ) {
        model.setDependencyManagement( dependencyManagement );
    }

    public String getVersion() {
        return model.getVersion();
    }

    public void removeContributor( Contributor contributor ) {
        model.removeContributor( contributor );
    }

    public void removeDeveloper( Developer developer ) {
        model.removeDeveloper( developer );
    }

    public void setDistributionManagement( DistributionManagement distributionManagement ) {
        model.setDistributionManagement( distributionManagement );
    }

    public void removeLicense( License license ) {
        model.removeLicense( license );
    }

    public void removeMailingList( MailingList mailingList ) {
        model.removeMailingList( mailingList );
    }

    public void setModules( List<String> modules ) {
        model.setModules( modules );
    }

    public void removeProfile( Profile profile ) {
        model.removeProfile( profile );
    }

    public void setArtifactId( String artifactId ) {
        model.setArtifactId( artifactId );
    }

    public void setPluginRepositories( List<Repository> pluginRepositories ) {
        model.setPluginRepositories( pluginRepositories );
    }

    public void setProperties( Properties properties ) {
        model.setProperties( properties );
    }

    public void setBuild( Build build ) {
        model.setBuild( build );
    }

    public void setChildProjectUrlInheritAppendPath( String childProjectUrlInheritAppendPath ) {
        model.setChildProjectUrlInheritAppendPath( childProjectUrlInheritAppendPath );
    }

    public void setReporting( Reporting reporting ) {
        model.setReporting( reporting );
    }

    public void setCiManagement( CiManagement ciManagement ) {
        model.setCiManagement( ciManagement );
    }

    public void setReports( Object reports ) {
        model.setReports( reports );
    }

    public void setRepositories( List<Repository> repositories ) {
        model.setRepositories( repositories );
    }

    public void setContributors( List<Contributor> contributors ) {
        model.setContributors( contributors );
    }

    public void setDescription( String description ) {
        model.setDescription( description );
    }

    public void setDevelopers( List<Developer> developers ) {
        model.setDevelopers( developers );
    }

    public void setGroupId( String groupId ) {
        model.setGroupId( groupId );
    }

    public void setInceptionYear( String inceptionYear ) {
        model.setInceptionYear( inceptionYear );
    }

    public void setIssueManagement( IssueManagement issueManagement ) {
        model.setIssueManagement( issueManagement );
    }

    public void setLicenses( List<License> licenses ) {
        model.setLicenses( licenses );
    }

    public void setMailingLists( List<MailingList> mailingLists ) {
        model.setMailingLists( mailingLists );
    }

    public void setModelEncoding( String modelEncoding ) {
        model.setModelEncoding( modelEncoding );
    }

    public void setModelVersion( String modelVersion ) {
        model.setModelVersion( modelVersion );
    }

    public void setName( String name ) {
        model.setName( name );
    }

    public void setOrganization( Organization organization ) {
        model.setOrganization( organization );
    }

    public void setPackaging( String packaging ) {
        model.setPackaging( packaging );
    }

    public void setParent( Parent parent ) {
        model.setParent( parent );
    }

    public void setPrerequisites( Prerequisites prerequisites ) {
        model.setPrerequisites( prerequisites );
    }

    public void setProfiles( List<Profile> profiles ) {
        model.setProfiles( profiles );
    }

    public void setScm( Scm scm ) {
        model.setScm( scm );
    }

    public void setUrl( String url ) {
        model.setUrl( url );
    }

    public void setVersion( String version ) {
        model.setVersion( version );
    }

    public File getPomFile() {
        return model.getPomFile();
    }

    public void setPomFile( File pomFile ) {
        model.setPomFile( pomFile );
    }

    public File getProjectDirectory() {
        return model.getProjectDirectory();
    }

    public String getId() {
        return model.getId();
    }

    public String toString() {
        return model.toString();
    }

    public boolean isChildProjectUrlInheritAppendPath() {
        return model.isChildProjectUrlInheritAppendPath();
    }

    public void setChildProjectUrlInheritAppendPath( boolean childProjectUrlInheritAppendPath ) {
        model.setChildProjectUrlInheritAppendPath( childProjectUrlInheritAppendPath );
    }

    public Model getModel() {
        return model;
    }

}
