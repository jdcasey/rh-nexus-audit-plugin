#!/usr/bin/ruby

require 'find'

include Find

class AuditInfo
  
  attr_writer :owner
  attr_writer :date
  attr_writer :repository_id
  attr_writer :referenced_path
  
  def valid?
    if @owner && @date && @repository_id && @referenced_path
      return true
    end
    
    return false
  end
  
  def to_json
    file = "{\n"
    file << '"owner":"' << @owner << '",' 
    file << "\n" << '"captured-on":"' << @date << '",' 
    file << "\n" << '"references-path":"' << @referenced_path << '",' 
    file << "\n" << '"repository-id":"' << @repository_id << '"'
    file << "\n}\n"
    
    return file
  end
  
  def self.generate_audit_info( dir )
    
    real_dir = File.expand_path( dir )
    dirname = File.basename( real_dir  )
    
    find( real_dir ) do |abs_path|
      
      rel_path = abs_path[real_dir.length + 1..-1]
      
      if ( !( abs_path['.svn'] ) && File.file?( abs_path ) )
        
        Dir.chdir( File.dirname( abs_path) ) do
          filename = File.basename( abs_path )
          audit_filename = filename + ".audit.json"
          if ( !File.exists?( audit_filename ) )

            # SAMPLE SVN INFO:
            #
            # Path: pom.xml
            # Name: pom.xml
            # URL: https://svn.apache.org/repos/asf/maven/doxia/trunks/pom.xml
            # Repository Root: https://svn.apache.org/repos/asf
            # Repository UUID: 13f79535-47bb-0310-9956-ffa450edef68
            # Revision: 833154
            # Node Kind: file
            # Schedule: normal
            # Last Changed Author: bentmann
            # Last Changed Rev: 782220
            # Last Changed Date: 2009-06-06 06:26:52 -0400 (Sat, 06 Jun 2009)
            # Text Last Updated: 2009-11-05 15:18:03 -0500 (Thu, 05 Nov 2009)
            # Checksum: 003021b452d5b03d16ce618d4dd8b643

            info = AuditInfo.new
            info.repository_id = dirname
            info.referenced_path = rel_path

            puts "In: #{Dir.pwd}"
            `svn info #{filename}`.each_line do |line|
              part = line.chomp
              if ( part =~ /.*Last Changed Author:\s+([-_+0-9a-zA-Z]+).*/ )
                info.owner = $1
              elsif ( part =~ /.*Last Changed Date:\s+([-:+0-9]+ [-:+0-9]+ [-:+0-9]+).*/ )
                info.date = $1
              end
            end

            if ( info.valid? )

              # SAMPLE JSON AUDIT FILE FORMAT:
              #
              # {"owner":"admin","captured-on":"Mar 23, 2010 11:01:43 AM","references-path":
              #   "/org/redhat/devel/jcasey/audit/jboss-release-dep/1.0/jboss-release-dep-1.0.jar",
              #   "repository-id":"jb-release"}

              begin
                File.open( audit_filename, 'w+' ) do |file|
                  file << info.to_json
                end # File.open
              rescue Exception => e
                puts "Failed to write file: '#{File.join( Dir.pwd, audit_filename )}'\n#{e}\n#{e.backtrace.join("\n")}"
              end # begin/rescue
            else
              puts "[#{dirname}] No SVN information for: #{rel_path}. Cannot construct audit info."
            end # if( info.valid? )
          end # if ( !File.exists?(..) )
        end # Dir.chdir
      end # if ( File.file?( path ) )
    end # find(..)
  end # self.generate_audit_info
  
end # AuditInfo class


if ( ARGV.length > 0 )
  ARGV.each do |dir|
    AuditInfo.generate_audit_info( dir )
  end
else
  AuditInfo.generate_audit_info( '.' )
end

