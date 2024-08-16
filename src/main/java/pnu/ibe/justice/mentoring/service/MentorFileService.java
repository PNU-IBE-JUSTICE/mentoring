package pnu.ibe.justice.mentoring.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pnu.ibe.justice.mentoring.domain.Mentor;
import pnu.ibe.justice.mentoring.domain.MentorFile;
import pnu.ibe.justice.mentoring.model.MentorFileDTO;
import pnu.ibe.justice.mentoring.repos.MentorFileRepository;
import pnu.ibe.justice.mentoring.repos.MentorRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;


@Service
public class MentorFileService {

    private final MentorFileRepository mentorFileRepository;
    private final MentorRepository mentorRepository;

    public MentorFileService(final MentorFileRepository mentorFileRepository,
            final MentorRepository mentorRepository) {
        this.mentorFileRepository = mentorFileRepository;
        this.mentorRepository = mentorRepository;
    }

    public List<MentorFileDTO> findAll() {
        final List<MentorFile> mentorFiles = mentorFileRepository.findAll(Sort.by("seqId"));
        return mentorFiles.stream()
                .map(mentorFile -> mapToDTO(mentorFile, new MentorFileDTO()))
                .toList();
    }

    public MentorFileDTO get(final Long seqId) {
        return mentorFileRepository.findById(seqId)
                .map(mentorFile -> mapToDTO(mentorFile, new MentorFileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final MentorFileDTO mentorFileDTO) {
        final MentorFile mentorFile = new MentorFile();
        mapToEntity(mentorFileDTO, mentorFile);
        return mentorFileRepository.save(mentorFile).getSeqId();
    }

    public void update(final Long seqId, final MentorFileDTO mentorFileDTO) {
        final MentorFile mentorFile = mentorFileRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(mentorFileDTO, mentorFile);
        mentorFileRepository.save(mentorFile);
    }

//    public Men downloadFile(long mFid){
//
//    }

    public void delete(final Long seqId) {
        mentorFileRepository.deleteById(seqId);
    }

    private MentorFileDTO mapToDTO(final MentorFile mentorFile, final MentorFileDTO mentorFileDTO) {
//        mentorFileDTO.setSeqId(mentorFile.getSeqId());
        mentorFileDTO.setFileSrc(mentorFile.getFileSrc());
        mentorFileDTO.setMentor(mentorFile.getMentor() == null ? null : mentorFile.getMentor().getSeqId());

        return mentorFileDTO;
    }

    private MentorFile mapToEntity(final MentorFileDTO mentorFileDTO, final MentorFile mentorFile) {
        mentorFile.setFileSrc(mentorFileDTO.getFileSrc());
        final Mentor mentor = mentorFileDTO.getMentor() == null ? null : mentorRepository.findById(mentorFileDTO.getMentor())
                .orElseThrow(() -> new NotFoundException("mentor not found"));
        mentorFile.setMentor(mentor);
        return mentorFile;
    }



}
